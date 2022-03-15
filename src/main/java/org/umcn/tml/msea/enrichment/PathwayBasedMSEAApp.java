package org.umcn.tml.msea.enrichment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.umcn.tml.msea.datafiles.filters.FeatureFilterSets;
import org.umcn.tml.msea.datafiles.headers.labels.FeatureTableLabel;
import org.umcn.tml.msea.datafiles.headers.labels.MSEAMetaboliteSetLabel;
import org.umcn.tml.msea.enrichment.hyperlink.KEGGPathwayHyperLinkGenerator;
import org.umcn.tml.msea.enrichment.hyperlink.SMPDBPathwayHyperLinkGenerator;
import org.umcn.tml.msea.enums.MSEABackground;
import org.umcn.tml.shared.statistics.FisherExactTest;
import org.umcn.tml.shared.databases.enums.PathwayDatabase;
import org.umcn.tml.shared.databases.headers.MetaboliteSetDBHeader;
import org.umcn.tml.shared.databases.headers.labels.MetaboliteSetDBLabel;
import org.umcn.tml.shared.datastructures.datatable.DataTable;
import org.umcn.tml.shared.datastructures.datatable.DataTableFilter;
import org.umcn.tml.shared.datastructures.datatable.DataTableRow;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterType;
import org.umcn.tml.shared.datastructures.datatable.textfile.TextFileParser;
import org.umcn.tml.shared.datastructures.datatable.textfile.TextFileWriter;
import org.umcn.tml.shared.datastructures.datatable.textfile.TextFileWriter.OriginalColumns;
import org.umcn.tml.shared.datastructures.datatable.textfile.enums.FieldSeparator;
import org.umcn.tml.shared.statistics.HypothesisTesting;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

@Component
public class PathwayBasedMSEAApp {
	
	private static File metaboliteSetDatabase;
	private static File smpdbMetaboliteIDs;
	private static File inputFeatureFile;
	private static File outputMetaboliteSetFile;
	private static File outputFeatureFile;
	
	private static final Logger logger = LoggerFactory.getLogger(PathwayBasedMSEAApp.class);
	
	public static void main (String[] args) throws IOException {
		ConfigurableApplicationContext springApplicationBuilder = new SpringApplicationBuilder()
				.sources(PathwayBasedMSEAConfig.class)
				.run(args);
		
		processArguments(args);
	
		PathwayBasedMSEAApp pathwayBasedMSEA = springApplicationBuilder.getBean(PathwayBasedMSEAApp.class);
		pathwayBasedMSEA.start(metaboliteSetDatabase, smpdbMetaboliteIDs, inputFeatureFile, outputFeatureFile, outputMetaboliteSetFile, MSEABackground.ALL);

		springApplicationBuilder.close();
	}
    
    public static void processArguments(String args[]) throws IOException{
    	String arg1 = "metaboliteSetDatabase";    	
    	String arg2 = "smpdbMetaboliteIDs";    	
    	String arg3 = "inputFeatureFile";
    	String arg4 = "outputMetaboliteSetFile";
    	String arg5 = "outputFeatureFile";
    	
    	OptionSet options;
    	OptionParser parser = new OptionParser(){
    		{
    			accepts(arg1,"Absolute path to the Metabolite Set Database file.")
    			.withRequiredArg().ofType(String.class).required();
    			
    			accepts(arg2,"Absolute path to the SMPDB Metabolite IDs file.")
    			.withRequiredArg().ofType(String.class).required();
    			
    			accepts(arg3,"Absolute path to the input feature file.")
    			.withRequiredArg().ofType(String.class).required();
    			
    			accepts(arg4,"Absolute path to the output metabolite set file.")
    			.withRequiredArg().ofType(String.class).required();
    			
    			accepts(arg5, "Absolute path to the output feature file.")
    			.withRequiredArg().ofType(String.class).required();
    		}
    	};
    	
    	try {
			options =  parser.parse(args);
		} catch (OptionException e) {
			logger.error("Invalid arguments:", e);
			parser.printHelpOn(System.out);
			throw e;
		}
    	
    	metaboliteSetDatabase = new File(options.valueOf(arg1).toString());  
    	smpdbMetaboliteIDs = new File(options.valueOf(arg2).toString());  
    	inputFeatureFile = new File(options.valueOf(arg3).toString());    	
    	outputMetaboliteSetFile = new File(options.valueOf(arg4).toString());  	
    	outputFeatureFile = new File(options.valueOf(arg5).toString());
    	
    	//TODO: Caused issues while running from the jar, but file can be read later on just fine
//    	if (!metaboliteSetDatabase.exists()) {
//    		throw new RuntimeException("Metabolite set database does not exist: " + metaboliteSetDatabase.getAbsolutePath());
//    	}
//    	if (!smpdbMetaboliteIDs.exists()) {
//    		throw new RuntimeException("Metabolite set database does not exist: " + metaboliteSetDatabase.getAbsolutePath());
//    	}
//    	if (!inputFeatureFile.exists()) {
//    		throw new RuntimeException("Input feature file does not exist: " + inputFeatureFile.getAbsolutePath());
//    	}
    }
	
	private final TextFileParser textFileParser;
	private final DataTableFilter dataTableFilter;
	private final KEGGPathwayHyperLinkGenerator keggPathwayHyperLinkGenerator;
	private final SMPDBPathwayHyperLinkGenerator smpdbPathwayHyperLinkGenerator;
	private final TextFileWriter textFileWriter;
	
	private final double udaAlpha;

	@Autowired	
	public PathwayBasedMSEAApp(
			TextFileParser textFileParser,
			DataTableFilter dataTableFilter,
			KEGGPathwayHyperLinkGenerator keggPathwayHyperLinkGenerator,
			SMPDBPathwayHyperLinkGenerator smpdbPathwayHyperLinkGenerator,
			TextFileWriter textFileWriter,
			@Value("${UDA_alpha}") double udaAlpha) 
	{
		this.textFileParser = textFileParser;
		this.dataTableFilter = dataTableFilter;
		this.keggPathwayHyperLinkGenerator = keggPathwayHyperLinkGenerator;
		this.smpdbPathwayHyperLinkGenerator = smpdbPathwayHyperLinkGenerator;
		this.textFileWriter = textFileWriter;
		
		this.udaAlpha = udaAlpha;
	}
	
	public void start(
			File metaboliteSetDatabase, 
			File smpdbMetaboliteIDs,
			File inputFeatureFile, 
			File outputFeatureFile,
			File outputMetaboliteSetFile,
			MSEABackground mseaBackground) throws IOException 
	{		
		
		logger.info("processing: " + inputFeatureFile.getName());
		DataTable dtMetaboliteSetDatabase = loadingMetaboliteSetDatabase(metaboliteSetDatabase);
		DataTable dtFeatureTable = loadingFeatureTable(inputFeatureFile);		
		DataTable dtMSEA = generatingResultDataTable(outputMetaboliteSetFile);
				
		dtFeatureTable = coupleFeaturesToMetaboliteSets(dtMetaboliteSetDatabase, dtFeatureTable);
		
		dtMSEA = generateMSEATable(mseaBackground, smpdbMetaboliteIDs, dtMetaboliteSetDatabase, dtFeatureTable, dtMSEA);
		
		textFileWriter.start(outputMetaboliteSetFile, true, dtMSEA, OriginalColumns.Dont_Include);			
		textFileWriter.start(outputFeatureFile, true, dtFeatureTable, OriginalColumns.First, FeatureTableLabel.MSEA_Metabolite_Set_ID);	
	}
	
	private DataTable loadingMetaboliteSetDatabase(File metaboliteSetDatabase) throws IOException 
	{
		logger.info("Loading Metabolite Set Database...");		
		DataTable dtMetaboliteSetDatabase = textFileParser.parseEssentialLabelColumns(metaboliteSetDatabase, new MetaboliteSetDBHeader(), FieldSeparator.TAB);
		return dtMetaboliteSetDatabase;
	}
	
	private DataTable loadingFeatureTable(File inputFeatureFile) throws IOException 
	{
		logger.info("Loading datafiles...");
		
		DataTable dtFeatureTable = textFileParser.parseEssentialLabelColumns(inputFeatureFile, new FeatureTableHeader(), FieldSeparator.TAB);
		dtFeatureTable.addColumn(FeatureTableLabel.MSEA_Metabolite_Set_ID);	
		
		return dtFeatureTable;
	}
	
	private DataTable generatingResultDataTable(File outputMetaboliteSetFile) throws IOException 
	{
		DataTable dtMetaboliteSetFile = new DataTable(outputMetaboliteSetFile.getName(), "");
		dtMetaboliteSetFile.addColumns(MSEAMetaboliteSetLabel.values());
		dtMetaboliteSetFile.removeColumn(MSEAMetaboliteSetLabel.Cluster_ID);
		return dtMetaboliteSetFile;
	}

	private DataTable coupleFeaturesToMetaboliteSets(
			DataTable dtMetaboliteSetDatabase, 
			DataTable dtFeatureTable) throws IOException 
	{	
		logger.info("Coupling metabolite set information to feature table...: ");
		
		DataTable dtFTMetaboliteAnnotations = dataTableFilter.filter(false, dtFeatureTable, 
				FeatureFilterSets.includeAnnotatedFeatures());

		for (DataTableRow dtrMetaboliteSet : dtMetaboliteSetDatabase.getRowList()) {		
			String msID = dtrMetaboliteSet.getString(MetaboliteSetDBLabel.ID);			
			String msMetabolites = dtrMetaboliteSet.getString(MetaboliteSetDBLabel.Metabolites);
			
			//If the metabolite set is not associated to any metabolites, it cannot be used for MSEA.
			if (msMetabolites.equals("")) continue;
			
			//Get all features that can be associated to this metabolite set.
			String[] msMetabolitesArray = dtrMetaboliteSet.getString(MetaboliteSetDBLabel.Metabolites).split(";");
			DataTable dtMSFeatures = dataTableFilter.filter(false, dtFTMetaboliteAnnotations, FeatureFilterSets.includeMetabolites(msMetabolitesArray));
			
			//Only test a metabolite set if it is connected to at least two aberrant features in our data.
			DataTable dtMSAberrantFeatures = dataTableFilter.filter(false, dtMSFeatures, FeatureFilterSets.includeAberrantFeatures(udaAlpha));
			Set<String> uniqueAberrantFeatures = dtMSAberrantFeatures.getStringColumnAsSet(FeatureTableLabel.Feature_ID);
			if (uniqueAberrantFeatures.size() < 2) continue;
			
			//Add this metabolite set to each feature in the feature table.
			for (DataTableRow dtrPathwayFeatureAnnotations : dtMSFeatures.getRowList()) {
				dtrPathwayFeatureAnnotations.appendStringField(FeatureTableLabel.MSEA_Metabolite_Set_ID, msID, ";");
			}						
		}
		
		return dtFeatureTable;
	}
	
	private DataTable generateMSEATable(
			MSEABackground mseaBackground,
			File smpdbMetaboliteIDs, 
			DataTable dtMetaboliteSetDatabase, 
			DataTable dtFeatureTable, 
			DataTable dtMSEA) throws IOException 
	{		
		logger.info("Generating MSEA Table...");
		
		dtFeatureTable = background(mseaBackground, dtFeatureTable);
		
		DataTable dtFTUniqueFeatures = dataTableFilter.getUniqueValueRows(false, dtFeatureTable, FeatureTableLabel.Feature_ID, false);
		DataTable dtFTU_AberrantFeatures = dataTableFilter.filter(false, dtFTUniqueFeatures, FeatureFilterSets.includeAberrantFeatures(udaAlpha));
		DataTable dtFTU_NormalFeatures = dataTableFilter.filter(false, dtFTUniqueFeatures, FeatureFilterSets.excludeAberrantFeatures(udaAlpha));	
		
		DataTable dtFTMetaboliteAnnotations = dataTableFilter.filter(false, dtFeatureTable, FeatureFilterSets.includeMSEAFeatures(PathwayDatabase.ALL));
		
		FisherExactTest fisherExactTest = new FisherExactTest(dtFTU_AberrantFeatures.size() + dtFTU_NormalFeatures.size());
		
		for (DataTableRow dtrMetaboliteSet : dtMetaboliteSetDatabase.getRowList()) {				
			String metaboliteSetID = dtrMetaboliteSet.getString(MetaboliteSetDBLabel.ID);
			String metaboliteSetName = dtrMetaboliteSet.getString(MetaboliteSetDBLabel.Name);			
			
			logger.info("Processing: " + metaboliteSetName + " (" + metaboliteSetID + ")");			

			//Only test metabolite set when it's connected to enough features in our data.
			if (!dtFTMetaboliteAnnotations.contains(FeatureTableLabel.MSEA_Metabolite_Set_ID, metaboliteSetID, false)) continue;

			//Add general information of metabolite set to MSEA table row.
			DataTableRow dtrMSEA = new DataTableRow("");			
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Metabolite_Set_ID, metaboliteSetID, true);
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Metabolite_Set_Name, metaboliteSetName, true);
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Metabolite_Set_Category, dtrMetaboliteSet.getString(MetaboliteSetDBLabel.Category), true);
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Metabolite_Set_Metabolites, dtrMetaboliteSet.getString(MetaboliteSetDBLabel.Metabolites), true);
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Metabolite_Set_Genes, dtrMetaboliteSet.getString(MetaboliteSetDBLabel.Genes), true);
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Associated_Metabolite_Set, dtrMetaboliteSet.getString(MetaboliteSetDBLabel.Associated_Metabolite_Sets), true);
			
			dtrMSEA = performFisherExact(fisherExactTest, metaboliteSetID, dtFTMetaboliteAnnotations, dtFTU_AberrantFeatures, dtFTU_NormalFeatures, dtrMSEA);
			
			//Generate hyperlink for visualization of data on pathway map.
			dtrMSEA = addPathwayHyperlink(smpdbMetaboliteIDs, dtrMetaboliteSet, dtrMSEA);		
			
			dtMSEA.addRow(false, dtrMSEA);
		}
					
		dtMSEA = multipleTestingCorrection(dtMSEA);	
		
		return dtMSEA;
	}
	
	private DataTable background(MSEABackground mseaBackground, DataTable dtFeatureTable) throws IOException {
    	switch(mseaBackground) {
		case ALL:
			//do nothing
			break;
		case METABOLITE_ANNOTATED:
			dtFeatureTable = dataTableFilter.filter(
					false, 
					dtFeatureTable, 
					FeatureFilterSets.includeAnnotatedFeatures());
			break;
		case TESTED_PATHWAY_ANNOTATED:
			dtFeatureTable = dataTableFilter.filter(
					false, 
					dtFeatureTable, 
					FeatureFilterSets.includeMSEAFeatures(PathwayDatabase.ALL));
			break;
		default:
			break;		
		}
    	
    	return dtFeatureTable;
	}
	
	private DataTableRow addPathwayHyperlink(
			File smpdbMetaboliteIDs, 
			DataTableRow dtrMetaboliteSet, 
			DataTableRow dtrMSEA) throws IOException 
	{
		PathwayDatabase pd = PathwayDatabase.getPathwayDatabase(dtrMetaboliteSet.getString(MetaboliteSetDBLabel.ID));
		switch(pd) {
		case KEGG:
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Visualization_Hyperlink, keggPathwayHyperLinkGenerator.start(dtrMSEA, true), true);
			return dtrMSEA;
		case SMPDB:
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Visualization_Hyperlink, smpdbPathwayHyperLinkGenerator.start(smpdbMetaboliteIDs, dtrMSEA, true), true);
			return dtrMSEA;
		default:
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Visualization_Hyperlink, "", true);
			return dtrMSEA;
		}		
	}
	
	private DataTableRow performFisherExact(
			FisherExactTest fisherExactTest,
			String msID,
			DataTable dtFTMetaboliteAnnotations, 
			DataTable dtFTUAberrantFeatures,
			DataTable dtFTUNormalFeatures,
			DataTableRow dtrMSEA) 
					throws IOException 
	{				
		//Filter feature table on metabolite set associated features
		DataTable dtFTMetaboliteSet = dataTableFilter.filter(false, dtFTMetaboliteAnnotations, FeatureFilterSets.includeMSEAFeatures(msID));
		
		//Select features associated to metabolite set
		Set<String> pathwayFeatureIDSet = dtFTMetaboliteSet.getStringColumnAsSet(FeatureTableLabel.Feature_ID);
		DataTable dtAberrantPathwayFeatures = dataTableFilter.filter(false, dtFTUAberrantFeatures, FeatureFilterSets.includeFeatures(pathwayFeatureIDSet));			
		DataTable dtNormalPathwayFeatures = dataTableFilter.filter(false, dtFTUNormalFeatures, FeatureFilterSets.includeFeatures(pathwayFeatureIDSet));
		
		//Select metabolites associated to metabolite set
		String[] aberrantPathwayFeatureIDArray = dtAberrantPathwayFeatures.getStringColumnAsArray(FeatureTableLabel.Feature_ID);	
		DataTable dtAberrantPathwayMetabolites = dataTableFilter.filter(false, dtFTMetaboliteSet, FeatureFilterSets.includeFeatures(aberrantPathwayFeatureIDArray));			
		DataTable dtNormalPathwayMetabolites;
		try {
			String[] normalPathwayFeatureIDArray = dtNormalPathwayFeatures.getStringColumnAsArray(FeatureTableLabel.Feature_ID);
			dtNormalPathwayMetabolites = dataTableFilter.filter(false, dtFTMetaboliteSet, FeatureFilterSets.includeFeatures(normalPathwayFeatureIDArray));		
		} catch (ArrayIndexOutOfBoundsException e) {
			dtNormalPathwayMetabolites = new DataTable(dtFTMetaboliteSet.getFileName(), dtFTMetaboliteSet.getHeaderSet(), "");
		}
		
		dtrMSEA.addField(MSEAMetaboliteSetLabel.Aberrant_Features, String.join(";", dtAberrantPathwayFeatures.getStringColumnAsSet(FeatureTableLabel.Feature_ID)), true);
		dtrMSEA.addField(MSEAMetaboliteSetLabel.Normal_Features, String.join(";", dtNormalPathwayFeatures.getStringColumnAsSet(FeatureTableLabel.Feature_ID)), true);
		
		if (dtrMSEA.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID).toLowerCase().startsWith("smp")) {				
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Aberrant_Metabolites, String.join(";", determineMetaboliteRegulation(dtAberrantPathwayMetabolites, FeatureTableLabel.HMP)), true);
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Normal_Metabolites, String.join(";", dtNormalPathwayMetabolites.getStringColumnAsSet(FeatureTableLabel.HMP)), true);
		} else {
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Aberrant_Metabolites, String.join(";", determineMetaboliteRegulation(dtAberrantPathwayMetabolites, FeatureTableLabel.KEGG_Entry)), true);
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Normal_Metabolites, String.join(";", dtNormalPathwayMetabolites.getStringColumnAsSet(FeatureTableLabel.KEGG_Entry)), true);				
		}
		
		Double highestFCIncrease = dtAberrantPathwayFeatures.getHighestDoubleValue(FeatureTableLabel.Fold_Change, false);
		if (highestFCIncrease <= 0) {
			highestFCIncrease = null;
		}
		dtrMSEA.addField(MSEAMetaboliteSetLabel.Highest_Feature_FC_Increase, highestFCIncrease, true);
		
		Double highestFCDecrease= dtAberrantPathwayFeatures.getLowestDoubleValue(FeatureTableLabel.Fold_Change, false);
		if (highestFCDecrease >= 0) {
			highestFCDecrease = null;
		}
		dtrMSEA.addField(MSEAMetaboliteSetLabel.Highest_Feature_FC_Decrease, highestFCDecrease, true);		
		
		dtrMSEA.addField(MSEAMetaboliteSetLabel.Feature_FC_NaNs, dataTableFilter.filter(false, dtAberrantPathwayFeatures, FeatureFilterSets.includeFoldChanges(Double.NaN, FilterType.EQUALS)).size(), true);
		
		int n11 = dtAberrantPathwayFeatures.size();
		int n21 = dtNormalPathwayFeatures.size();		   		
		int n12 = dtFTUAberrantFeatures.size() - n11;
		int n22 = dtFTUNormalFeatures.size() - n21;	
		
		double fisherExact = fisherExactTest.calculateHypergeomP(n11, n12, n21, n22);

		dtrMSEA.addField(MSEAMetaboliteSetLabel.Hypergeometric_Test_n11, n11, true);
		dtrMSEA.addField(MSEAMetaboliteSetLabel.Hypergeometric_Test_n21, n21, true);
		dtrMSEA.addField(MSEAMetaboliteSetLabel.Hypergeometric_Test_n12, n12, true);
		dtrMSEA.addField(MSEAMetaboliteSetLabel.Hypergeometric_Test_n22, n22, true);
		
		dtrMSEA.addField(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value, fisherExact, true);  
		
		return dtrMSEA;		
	}
    
    public Set<String> determineMetaboliteRegulation(
    		DataTable dtAberrantPathwayMetabolites, 
    		FeatureTableLabel label) 
    {
    	String UP = "[U]";
    	String DOWN = "[D]";
    	String Both = "[?]"; //multiple features can be associated to a metabolite, these can be aberrant in different directions.
    	
		Set<String> normalPathwayMetaboliteSet = new HashSet<String>();
		for (DataTableRow dtrAberrantPathwayMetabolites : dtAberrantPathwayMetabolites.getRowList()) {
			String metaboliteID = dtrAberrantPathwayMetabolites.getString(label);
			if (dtrAberrantPathwayMetabolites.getDouble(FeatureTableLabel.Fold_Change) > 0) {
				if (normalPathwayMetaboliteSet.contains(metaboliteID+DOWN)) {
					normalPathwayMetaboliteSet.add(metaboliteID+Both);
					normalPathwayMetaboliteSet.remove(metaboliteID+DOWN);
				}else if (!normalPathwayMetaboliteSet.contains(metaboliteID+Both)) {
					normalPathwayMetaboliteSet.add(metaboliteID+UP);
				}
			} else if (dtrAberrantPathwayMetabolites.getDouble(FeatureTableLabel.Fold_Change) < 0) {
				if (normalPathwayMetaboliteSet.contains(metaboliteID+UP)) {
					normalPathwayMetaboliteSet.add(metaboliteID+Both);
					normalPathwayMetaboliteSet.remove(metaboliteID+UP);
				}else if (!normalPathwayMetaboliteSet.contains(metaboliteID+Both)) {
					normalPathwayMetaboliteSet.add(metaboliteID+DOWN);
				}
			} else {
				normalPathwayMetaboliteSet.add(metaboliteID+Both);
				normalPathwayMetaboliteSet.remove(metaboliteID+UP);
				normalPathwayMetaboliteSet.remove(metaboliteID+DOWN);
			}
		}
		return normalPathwayMetaboliteSet;
	}
    
    public DataTable multipleTestingCorrection(DataTable dtMSEA) throws IOException 
    {
    	dtMSEA.addColumn(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value_Benjamini_Hochberg);
		dtMSEA.addColumn(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value_Bonferoni_Holm);
		
		Map<String, Double> pValueMap = new HashMap<String, Double>();
		for (DataTableRow dtrMSEA : dtMSEA.getRowList()) {			
			pValueMap.put(dtrMSEA.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID), dtrMSEA.getDouble(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value));			
		}
		
		HypothesisTesting ht = new HypothesisTesting();
		Map<String, Double> pValueBenjaminiHochbergMap = ht.benjaminiHochbergCorrection(pValueMap);
		Map<String, Double> pValueBonferoniHolmMap = ht.bonferroniHolmCorrection(pValueMap);
		
		for (DataTableRow dtrMSEA : dtMSEA.getRowList()) {
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value_Benjamini_Hochberg, pValueBenjaminiHochbergMap.get(dtrMSEA.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID)), true);
			dtrMSEA.addField(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value_Bonferoni_Holm, pValueBonferoniHolmMap.get(dtrMSEA.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID)), true);
		}
		
		return dtMSEA;
	}
}

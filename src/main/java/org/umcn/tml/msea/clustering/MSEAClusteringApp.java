package org.umcn.tml.msea.clustering;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.umcn.tml.shared.statistics.FisherExactTest;
import org.umcn.tml.shared.statistics.HypothesisTesting;
import org.umcn.tml.msea.datafiles.enums.MSEAStatisticalFilterLevel;
import org.umcn.tml.msea.datafiles.headers.labels.FeatureTableLabel;
import org.umcn.tml.msea.datafiles.filters.FeatureFilterSets;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.umcn.tml.msea.datafiles.filters.MSEAMetaboliteSetFilterSets;
import org.umcn.tml.msea.datafiles.headers.labels.MSEAClusterLabel;
import org.umcn.tml.msea.datafiles.headers.labels.MSEAMetaboliteSetLabel;
import org.umcn.tml.shared.databases.enums.PathwayDatabase;
import org.umcn.tml.shared.datastructures.datatable.DataTable;
import org.umcn.tml.shared.datastructures.datatable.DataTableFilter;
import org.umcn.tml.shared.datastructures.datatable.DataTableRow;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterType;
import org.umcn.tml.shared.datastructures.datatable.textfile.TextFileParser;
import org.umcn.tml.shared.datastructures.datatable.textfile.TextFileWriter;
import org.umcn.tml.shared.datastructures.datatable.textfile.TextFileWriter.OriginalColumns;
import org.umcn.tml.shared.datastructures.datatable.textfile.enums.FieldSeparator;

@Component
public class MSEAClusteringApp {
	public enum ClusterStatistics {
		Original_MSEA_P_VALUE, 
		Representative_Pathway_Based, 
		Cluster_Based;
	}

	private static File inputMetaboliteSetFile;
	private static File inputFeatureFile;
	private static File outputClusterFile;
	private static File outputMetaboliteSetFile;
	private static File outputFeatureFile;

	private static final Logger logger = LoggerFactory.getLogger(MSEAClusteringApp.class);

	public static void main(String[] args ) throws IOException {
		ConfigurableApplicationContext springApplicationBuilder = new SpringApplicationBuilder()
				.sources(MSEAClusteringConfig.class)
				.run(args);
		
		processArguments(args);
		
		MSEAClusteringApp mseaClusteringApp = springApplicationBuilder.getBean(MSEAClusteringApp.class);

		mseaClusteringApp.start(ClusterStatistics.Original_MSEA_P_VALUE, MSEAStatisticalFilterLevel.BH, inputFeatureFile, inputMetaboliteSetFile, outputFeatureFile, outputMetaboliteSetFile, outputClusterFile);
		springApplicationBuilder.close();
	}
    
    public static void processArguments(String args[]) throws IOException{
    	String arg1 = "inputMetaboliteSetFile";    	
    	String arg2 = "inputFeatureFile";    	
    	String arg3 = "outputClusterFile";
    	String arg4 = "outputMetaboliteSetFile";
    	String arg5 = "outputFeatureFile";
    	
    	OptionSet options;
    	OptionParser parser = new OptionParser(){
    		{
    			accepts(arg1,"Absolute path to the input metabolite set file.")
    			.withRequiredArg().ofType(String.class).required();
    			
    			accepts(arg2,"Absolute path to the input feature file.")
    			.withRequiredArg().ofType(String.class).required();
    			
    			accepts(arg3,"Absolute path to the output cluster file.")
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
    	
    	inputMetaboliteSetFile = new File(options.valueOf(arg1).toString());  
    	inputFeatureFile = new File(options.valueOf(arg2).toString());  
    	outputClusterFile = new File(options.valueOf(arg3).toString());    	
    	outputMetaboliteSetFile = new File(options.valueOf(arg4).toString());  	
    	outputFeatureFile = new File(options.valueOf(arg5).toString());
    	
    	//TODO: Caused issues while running from the jar, but file can be read later on just fine
//    	if (!inputMetaboliteSetFile.exists()) {
//    		throw new RuntimeException("MSEA metabolite set file does not exist: " + inputMetaboliteSetFile.getAbsolutePath());
//    	}
//    	if (!inputFeatureFile.exists()) {
//    		throw new RuntimeException("MSEA feature file does not exist: " + inputFeatureFile.getAbsolutePath());
//    	}
    }

	protected final String appRunDate;
	protected final TextFileParser textFileParser;
	protected final DataTableFilter dataTableFilter;
	protected final TextFileWriter textFileWriter;	
	protected final HypothesisTesting ht;
	
	protected final double udaAlpha;
	protected final double mseaAlpha;		
	protected final int minIntersectionPercentage;	
	
	@Autowired
	public MSEAClusteringApp(
			String appRunDate,
			TextFileParser textFileParser,
			DataTableFilter dataTableFilter,
			TextFileWriter textFileWriter,
			HypothesisTesting ht,
			@Value("${UDA_alpha}") double udaAlpha,
			@Value("${MSEA_alpha}") double mseaAlpha,
			@Value("${MSEA_Clustering_Min_Intersection_Percentage}") int minIntersectionPercentage)
	{
		this.appRunDate = appRunDate;
		this.textFileParser = textFileParser;
		this.dataTableFilter = dataTableFilter;
		this.textFileWriter = textFileWriter;
		this.ht = ht;
		
		this.udaAlpha = udaAlpha;
		this.mseaAlpha = mseaAlpha;
		this.minIntersectionPercentage = minIntersectionPercentage;
	}	
	
	public void start(
			ClusterStatistics clusterStatistics,
			MSEAStatisticalFilterLevel sflMetaboliteSets,
			File mseaFeatureFile,
			File mseaMetaboliteSetFile,
			File mseaClusteringFeatureResultFile,
			File mseaClusteringMetaboliteSetResultFile,
			File mseaClusteringClusterResultFile
			) throws IOException {

		logger.info("Load input data...");
		DataTable dataTableMergedUDA = textFileParser.parseEssentialLabelColumns(mseaFeatureFile, new MSEASampleFeatureFileHeader(), FieldSeparator.TAB);
		dataTableMergedUDA.addColumns(FeatureTableLabel.MSEA_Cluster_ID);
		DataTable dataTableMSEAMetaboliteSet = textFileParser.parseEssentialLabelColumns(mseaMetaboliteSetFile, new MSEASampleMetaboliteSetFileHeader(), FieldSeparator.TAB);
		dataTableMSEAMetaboliteSet.addColumns(MSEAMetaboliteSetLabel.Cluster_ID);
		DataTable dataTableMSEAEnrichedMetaboliteSet = dataTableFilter.filter(false, dataTableMSEAMetaboliteSet, MSEAMetaboliteSetFilterSets.includeEnrichedMetaboliteSets(sflMetaboliteSets, mseaAlpha));

		List<MetaboliteSetCluster> metaboliteSetClusterList = new ArrayList<>();

		logger.info("Perform clustering of metabolite sets...");
		mseaClustering(dataTableMSEAEnrichedMetaboliteSet, metaboliteSetClusterList);
		
		dataTableMergedUDA = processingUDA(clusterStatistics, appRunDate, dataTableMergedUDA, metaboliteSetClusterList);
		
		DataTable dataTableMSEACluster = generateClusterDataTable(clusterStatistics, metaboliteSetClusterList, dataTableMergedUDA);
		dataTableMSEACluster = multipleTestingCorrection(clusterStatistics, dataTableMSEACluster);
		dataTableMSEACluster = identifySimilarClusters(clusterStatistics, dataTableMSEACluster);

		logger.info("Write clustering results to output files...");
		textFileWriter.start(mseaClusteringFeatureResultFile, true, dataTableMergedUDA, OriginalColumns.First, FeatureTableLabel.MSEA_Cluster_ID);	
		textFileWriter.start(mseaClusteringMetaboliteSetResultFile, true, dataTableMSEAMetaboliteSet, OriginalColumns.First, MSEAMetaboliteSetLabel.Cluster_ID);
		textFileWriter.start(mseaClusteringClusterResultFile, true, dataTableMSEACluster, OriginalColumns.Dont_Include);
	}

	protected void mseaClustering(
			DataTable dataTableMSEAMetaboliteSet, 
			List<MetaboliteSetCluster> metaboliteSetClusterList
			) throws IOException 
	{
		int clusterCount = 1;
		for (DataTableRow metaboliteSet : dataTableMSEAMetaboliteSet.getRowList()) {
			String[] aberrantMetabolitesMS = metaboliteSet.getString(MSEAMetaboliteSetLabel.Aberrant_Metabolites).split(";");
			Set<String> aberrantMetabolitesMSSet = new LinkedHashSet<>(Arrays.asList(aberrantMetabolitesMS));
			
			double intersectionPercentage = -1;
			
			MetaboliteSetCluster msc = null;
			//Find a cluster with #% overlap in aberrant metabolites.
			for (MetaboliteSetCluster currentMsc : metaboliteSetClusterList) {
				Set<String> aberrantMetabolitesCluster = currentMsc.getAberrantMetaboliteSet(ClusterStatistics.Cluster_Based);
				
				double currentIntersectionPercentage = determineIntersectionPercentage(aberrantMetabolitesCluster, aberrantMetabolitesMSSet);
				
				if (currentIntersectionPercentage >= minIntersectionPercentage && 
						currentIntersectionPercentage > intersectionPercentage) {
					intersectionPercentage = currentIntersectionPercentage;
					msc = currentMsc;
				}
			}			
			//Or create a new cluster.
			if (msc == null) {
				msc = new MetaboliteSetCluster(clusterCount++);
				metaboliteSetClusterList.add(msc);
			}
			
			//So we can do statistics on most informative metabolite set.
			msc.setRepresentativeMetaboliteSet(metaboliteSet);
			
			//Add information to cluster!
			msc.setAberrantFeatures(metaboliteSet.getString(MSEAMetaboliteSetLabel.Aberrant_Features).split(";"));
			msc.setAberrantMetabolites(metaboliteSet.getString(MSEAMetaboliteSetLabel.Aberrant_Metabolites).split(";"));
			msc.setHighestFCdecrease(metaboliteSet.getDouble(MSEAMetaboliteSetLabel.Highest_Feature_FC_Decrease));
			msc.setHighestFCIncrease(metaboliteSet.getDouble(MSEAMetaboliteSetLabel.Highest_Feature_FC_Increase));
			msc.setMetaboliteSetCategories(metaboliteSet.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID), metaboliteSet.getString(MSEAMetaboliteSetLabel.Metabolite_Set_Category));
			msc.setMetaboliteSets(metaboliteSet.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID), metaboliteSet.getString(MSEAMetaboliteSetLabel.Metabolite_Set_Name));
			msc.setNormalFeatures(metaboliteSet.getString(MSEAMetaboliteSetLabel.Normal_Features).split(";"));
			msc.setNormalMetabolites(metaboliteSet.getString(MSEAMetaboliteSetLabel.Normal_Metabolites).split(";"));
			msc.setKeyWords(metaboliteSet.getString(MSEAMetaboliteSetLabel.Metabolite_Set_Name));
			
			//Add information to MSEA Metabolite Set File.
			metaboliteSet.addField(MSEAMetaboliteSetLabel.Cluster_ID, msc.getID(), true);			
		}
	}
	
	protected DataTable processingUDA(
			ClusterStatistics clusterStatistics,
			String appRunDate,
			DataTable dataTableMergedUDA,
			List<MetaboliteSetCluster> metaboliteSetClusterList
			) throws IOException 
	{
		DataTable dataTableMergedUDAMSEA = dataTableFilter.filter(false, dataTableMergedUDA, FeatureFilterSets.includeMSEAFeatures(PathwayDatabase.ALL));
		for (MetaboliteSetCluster msc : metaboliteSetClusterList) {
			String[] pathwayArray = msc.getMetaboliteSetIDs().split(";");			
			
			DataTable dtClusterFeatures = dataTableFilter.filter(false, dataTableMergedUDAMSEA, FeatureFilterSets.includeMSEAFeatures(pathwayArray));
			DataTable dtUniqueClusterFeatures = dataTableFilter.getUniqueValueRows(false, dtClusterFeatures, FeatureTableLabel.Feature_ID, false);
			
			int naNCount = dataTableFilter.filter(false, dtUniqueClusterFeatures, FeatureFilterSets.includeFoldChanges(Double.NaN, FilterType.EQUALS)).size();
			msc.setFcNans(naNCount);	
			
			for (DataTableRow row : dtClusterFeatures.getRowList()) {
				row.appendStringField(FeatureTableLabel.MSEA_Cluster_ID, msc.getID(), ";");
			}
		}
		return dataTableMergedUDA;
	}
	
	@SuppressWarnings("incomplete-switch")
	protected DataTable generateClusterDataTable(
			ClusterStatistics clusterStatistics,
			List<MetaboliteSetCluster> metaboliteSetClusterList,
			DataTable dtMergedUDA) throws IOException 
	{
		DataTable dataTableClusterResult = new DataTable("ClusterResults", "");
		dataTableClusterResult.addColumns(MSEAClusterLabel.values());

		FisherExactTest fisherExactTest = null;
		switch(clusterStatistics) {
		case Cluster_Based:
			int uniqueFeatures = dataTableFilter.getUniqueValueRows(false, dtMergedUDA, FeatureTableLabel.Feature_ID, false).size();
			fisherExactTest = new FisherExactTest(uniqueFeatures);
			break;
		}

		for (MetaboliteSetCluster msc: metaboliteSetClusterList) {
			//Generate cluster row
			DataTableRow rowClusterResult = new DataTableRow("");
			rowClusterResult.addField(MSEAClusterLabel.Cluster_ID, msc.getID(), false);
			rowClusterResult.addField(MSEAClusterLabel.Keywords, msc.getKeyWords(), false);		
			rowClusterResult.addField(MSEAClusterLabel.Aberrant_Features, msc.getAberrantFeatures(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.Normal_Features, msc.getNormalFeatures(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.Aberrant_Metabolites, msc.getAberrantMetabolites(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.Normal_Metabolites, msc.getNormalMetabolites(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.Highest_FC_Increase, msc.getHighestFCIncrease(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.Highest_FC_Decrease, msc.getHighestFCdecrease(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.FC_NaNs, msc.getFcNans(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.Hypergeometric_Test_n11, msc.getN11(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.Hypergeometric_Test_n12, msc.getN12(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.Hypergeometric_Test_n21, msc.getN21(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.Hypergeometric_Test_n22, msc.getN22(clusterStatistics), false);
			rowClusterResult.addField(MSEAClusterLabel.Hypergeometric_Test_P_Value, msc.getHgtPvalue(clusterStatistics, fisherExactTest), false);	
			rowClusterResult.addField(MSEAClusterLabel.Metabolite_Set_IDs, msc.getMetaboliteSetIDs(), false);
			rowClusterResult.addField(MSEAClusterLabel.Metabolite_Set_Names, msc.getMetaboliteSetNames(), false);
			rowClusterResult.addField(MSEAClusterLabel.Metabolite_Set_Categories, msc.getMetaboliteSetCategories(), false);				

			switch(clusterStatistics) {
			case Original_MSEA_P_VALUE:
				rowClusterResult.addField(MSEAClusterLabel.Hypergeometric_Test_P_Value_Benjamini_Hochberg, msc.getRepresentativeMetaboliteSet().getDouble(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value_Benjamini_Hochberg), false);
				rowClusterResult.addField(MSEAClusterLabel.Hypergeometric_Test_P_Value_Bonferoni_Holm, msc.getRepresentativeMetaboliteSet().getDouble(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value_Bonferoni_Holm), false);
			case Representative_Pathway_Based:
				rowClusterResult.addField(MSEAClusterLabel.Representative_Metabolite_Set_ID, msc.getRepresentativeMetaboliteSetID(), false);
				rowClusterResult.addField(MSEAClusterLabel.Representative_Metabolite_Set_Name, msc.getRepresentativeMetaboliteSetName(), false);
				rowClusterResult.addField(MSEAClusterLabel.Representative_Metabolite_Set_Category, msc.getRepresentativeMetaboliteSetCategory(), false);	
				break;			
			}

			dataTableClusterResult.addRow(false, rowClusterResult);					
		}	
		return dataTableClusterResult;
	}
	
	@SuppressWarnings("incomplete-switch")
	protected DataTable multipleTestingCorrection(
			ClusterStatistics clusterStatistics,
			DataTable dtMSEACluster
			) throws IOException 
	{				

		switch(clusterStatistics) {
		case Cluster_Based:
		case Representative_Pathway_Based:
			Map<String, Double> pValueMap = new LinkedHashMap<>();
			for (DataTableRow dtrMSEACluster : dtMSEACluster.getRowList()) {
				String id = dtrMSEACluster.getString(MSEAClusterLabel.Cluster_ID);
				double pValue = dtrMSEACluster.getDouble(MSEAClusterLabel.Hypergeometric_Test_P_Value);
				pValueMap.put(id, pValue);
			}

			HypothesisTesting ht = new HypothesisTesting();
			Map<String, Double> pValueBenjaminiHochbergMap = ht.benjaminiHochbergCorrection(pValueMap);
			Map<String, Double> pValueBonferoniHolmMap = ht.bonferroniHolmCorrection(pValueMap);

			for (DataTableRow dtrMSEACluster : dtMSEACluster.getRowList()) {
				String id = dtrMSEACluster.getString(MSEAClusterLabel.Cluster_ID);
				dtrMSEACluster.addField(MSEAClusterLabel.Hypergeometric_Test_P_Value_Benjamini_Hochberg, pValueBenjaminiHochbergMap.get(id), true);
				dtrMSEACluster.addField(MSEAClusterLabel.Hypergeometric_Test_P_Value_Bonferoni_Holm, pValueBonferoniHolmMap.get(id), true);
			}
			break;	
		}
		return dtMSEACluster;
	}
	
	protected DataTable identifySimilarClusters(
			ClusterStatistics clusterStatistics,
			DataTable dtMSEACluster) throws IOException 
	{
		for (int i = 0; i < dtMSEACluster.size(); i++) {
			DataTableRow mscA = dtMSEACluster.getRowList().get(i);
			for (int j = i+1; j < dtMSEACluster.size(); j++) {
				DataTableRow mscB = dtMSEACluster.getRowList().get(j);				

				Set<String> aberrantFeaturesA = new LinkedHashSet<>();
				Set<String> aberrantFeaturesB = new LinkedHashSet<>();
				Collections.addAll(aberrantFeaturesA, mscA.getString(MSEAClusterLabel.Aberrant_Features).split(";"));
				Collections.addAll(aberrantFeaturesB, mscB.getString(MSEAClusterLabel.Aberrant_Features).split(";"));
				double currentIntersectionPercentage = determineIntersectionPercentage(aberrantFeaturesA, aberrantFeaturesB);
				if (currentIntersectionPercentage > 0) {
					mscA.appendStringField(MSEAClusterLabel.Similar_Clusters, mscB.getString(MSEAClusterLabel.Cluster_ID), ";");
					mscB.appendStringField(MSEAClusterLabel.Similar_Clusters, mscA.getString(MSEAClusterLabel.Cluster_ID), ";");
				}
			}
		}
		return dtMSEACluster;
	}

	protected int determineIntersection(Set<String> set1, Set<String> set2) {
		Set<String> separateSet1 = new LinkedHashSet<>(set1);
		separateSet1.removeAll(set2);
		int intersection = set1.size()-separateSet1.size();

		return intersection;
	}

	protected double determineIntersectionPercentage(Set<String> set1, Set<String> set2) {		
		double intersection = determineIntersection(set1, set2);

		double setSize;
		if (set1.size() < set2.size()) {
			setSize = set1.size();
		} else {
			setSize = set2.size();
		}
		double IntersectionPercentage = (intersection / setSize)*100;	

		return IntersectionPercentage;
	}
}

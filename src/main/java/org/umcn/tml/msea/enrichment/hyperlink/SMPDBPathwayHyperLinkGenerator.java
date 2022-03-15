package org.umcn.tml.msea.enrichment.hyperlink;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.umcn.tml.msea.datafiles.headers.labels.MSEAMetaboliteSetLabel;
import org.umcn.tml.shared.databases.headers.labels.SMPDBMetaboliteIDLabel;
import org.umcn.tml.shared.datastructures.datatable.DataTable;
import org.umcn.tml.shared.datastructures.datatable.DataTableRow;
import org.umcn.tml.shared.datastructures.datatable.textfile.TextFileParser;
import org.umcn.tml.shared.datastructures.datatable.textfile.enums.FieldSeparator;

@Component
public class SMPDBPathwayHyperLinkGenerator {
	
	private final TextFileParser textFileParser;
	
	@Autowired
	public SMPDBPathwayHyperLinkGenerator(
			TextFileParser textFileParser) 
	{
		this.textFileParser = textFileParser;		
	}
	
	private final Map<String, String> metaboliteIDMap = new LinkedHashMap<String, String>();
	
	public void loadSMPDBMetaboliteIDDatabase(File smpdbMetaboliteIDDatabase) throws IOException {
		DataTable smpdbMetaboliteIDTable = textFileParser.parseEssentialLabelColumns(smpdbMetaboliteIDDatabase, new SMPDBMetaboliteIDHeader(), FieldSeparator.TAB);
		
		for (DataTableRow row : smpdbMetaboliteIDTable.getRowList()) {
			String smpdb = row.getString(SMPDBMetaboliteIDLabel.SMPDB_ID);
			String hmdb = row.getString(SMPDBMetaboliteIDLabel.HMDB_ID);
			
			metaboliteIDMap.put(hmdb, smpdb);
		}
	}
	
	public String start(File smpdbMetaboliteIDDatabase, DataTableRow dataTableRowMSEA, boolean showInsignificant) throws IOException {
		
		if (metaboliteIDMap.isEmpty()) {
			loadSMPDBMetaboliteIDDatabase(smpdbMetaboliteIDDatabase);
		}
		
		String[] metaboliteSet = dataTableRowMSEA.getString(MSEAMetaboliteSetLabel.Metabolite_Set_Metabolites).split(";");
		String aberrantMetabolites = dataTableRowMSEA.getString(MSEAMetaboliteSetLabel.Aberrant_Metabolites);
		String normalMetabolites = dataTableRowMSEA.getString(MSEAMetaboliteSetLabel.Normal_Metabolites);
				
		String hyperlink = "=HYPERLINK(\"http://smpdb.ca/view/" + dataTableRowMSEA.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID) + "?";
		//analyze[compounds][PW_C002028]=1&analyze[compounds][PW_C002913]=2&analyze[compounds][PW_C002907]=3&analyze[compounds][PW_C002910]=5
		for (String hmdbID : metaboliteSet) {
			//todo: get smpdbID to map metabolite to pathway.
			String smpdbID = metaboliteIDMap.get(hmdbID);
			if (aberrantMetabolites.contains(hmdbID + "[U]")) {
				hyperlink=hyperlink+"analyze[compounds]["+smpdbID+"]=1&";		
			} else if (aberrantMetabolites.contains(hmdbID + "[D]")) {
				hyperlink=hyperlink+"analyze[compounds]["+smpdbID+"]=3&";
			} else if (aberrantMetabolites.contains(hmdbID + "[?]")) {
				hyperlink=hyperlink+"analyze[compounds]["+smpdbID+"]=2&";		
			} else if (showInsignificant && normalMetabolites.contains(hmdbID)) {
				hyperlink=hyperlink+"analyze[compounds]["+smpdbID+"]=5&";		
			}
		}
		
		return hyperlink+"\", \""+dataTableRowMSEA.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID)+"\")"; //TODO: replace final & ?
	}
}

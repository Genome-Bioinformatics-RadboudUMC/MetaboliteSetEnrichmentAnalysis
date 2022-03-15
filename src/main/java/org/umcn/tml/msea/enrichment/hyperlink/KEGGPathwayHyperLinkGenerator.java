package org.umcn.tml.msea.enrichment.hyperlink;

import org.springframework.stereotype.Component;
import org.umcn.tml.msea.datafiles.headers.labels.MSEAMetaboliteSetLabel;
import org.umcn.tml.shared.datastructures.datatable.DataTableRow;

@Component
public class KEGGPathwayHyperLinkGenerator {
	public String start(DataTableRow dataTableRowMSEA, boolean showInsignificant) {
		String hyperlink = "=HYPERLINK(\"https://www.kegg.jp/kegg-bin/show_pathway?" + dataTableRowMSEA.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID);
		
		String hyperlinkInsignificant = "";
		for (String insignificantMetabolite : dataTableRowMSEA.getString(MSEAMetaboliteSetLabel.Normal_Metabolites).split(";")) {
			hyperlinkInsignificant=hyperlink+"/"+insignificantMetabolite+"%09"+"%23"+"D6C8E5"+","; //grey
		}
		
		String hyperlinkSignificant = "";
		for (String significantMetabolite : dataTableRowMSEA.getString(MSEAMetaboliteSetLabel.Aberrant_Metabolites).split(";")) {
			if (significantMetabolite.endsWith("[U]")) { //up regulated
				significantMetabolite = significantMetabolite.replaceAll("\\[.\\]", "");
				if (hyperlinkInsignificant.contains(significantMetabolite)) {
					hyperlinkInsignificant = hyperlinkInsignificant.replace(significantMetabolite+"%09"+"%23"+"D6C8E5", "");
					hyperlinkSignificant=hyperlinkSignificant+"/"+significantMetabolite+"%09"+"%23"+"FF7575"+","; //light red
				} else {
					hyperlinkSignificant=hyperlinkSignificant+"/"+significantMetabolite+"%09"+"%23"+"FF0000"+","; //red
				}
			} else if (significantMetabolite.endsWith("[D]")) { //down regulated
				significantMetabolite = significantMetabolite.replaceAll("\\[.\\]", "");
				if (hyperlinkInsignificant.contains(significantMetabolite)) { 
					hyperlinkInsignificant=hyperlinkInsignificant.replace(significantMetabolite+"%09"+"%23"+"D6C8E5", "");		
					hyperlinkSignificant=hyperlinkSignificant+"/"+significantMetabolite+"%09"+"%23"+"FFFF75"+","; //light yellow	
				} else {
					hyperlinkSignificant=hyperlinkSignificant+"/"+significantMetabolite+"%09"+"%23"+"FFFF00"+","; //yellow
				}
			} else { //up or down regulated
				significantMetabolite = significantMetabolite.replaceAll("\\[.\\]", "");
				if (hyperlinkInsignificant.contains(significantMetabolite)) { 
					hyperlinkInsignificant=hyperlinkInsignificant.replace(significantMetabolite+"%09"+"%23"+"D6C8E5", "");
					hyperlinkSignificant=hyperlinkSignificant+"/"+significantMetabolite+"%09"+"%23"+"FFCC75"+","; //light orange
				} else {
					hyperlinkSignificant=hyperlinkSignificant+"/"+significantMetabolite+"%09"+"%23"+"FFA200"+","; //orange
				}
			}
		}
		
		hyperlink = hyperlink + hyperlinkSignificant;
		
		if (showInsignificant) {
			hyperlink = hyperlink + hyperlinkInsignificant;
		}
		
		return hyperlink+"\", \""+dataTableRowMSEA.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID)+"\")";
	}
}

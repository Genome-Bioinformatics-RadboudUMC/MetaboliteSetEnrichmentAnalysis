package org.umcn.tml.shared.databases.enums;

public enum SMPDBPathwayCategories {
	Metabolic("Metabolic"),
	Disease("Disease"),
	Drug_Action("Drug Action"),
	Physiological("Physiological"),
	Signaling("Signaling"),
	Protein("Protein"),
	Drug_Metabolism("Drug Metabolism");
	
	private final String name;
	
	SMPDBPathwayCategories(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}

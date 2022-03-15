package org.umcn.tml.msea.enums;

//X features are taken along for the Fisher Exact's outside pathway calculations.
public enum MSEABackground {
	ALL, 
	METABOLITE_ANNOTATED,
	TESTED_PATHWAY_ANNOTATED; //tested pathway = a pathway taken along for the MSEA.
	
	public static MSEABackground get(String setting) {
		if (setting.equals(MSEABackground.ALL.name())) {
			return MSEABackground.ALL;
		} else if (setting.equals(MSEABackground.METABOLITE_ANNOTATED.name())) {
			return MSEABackground.METABOLITE_ANNOTATED;
		} else if (setting.equals(MSEABackground.TESTED_PATHWAY_ANNOTATED.name())) {
			return MSEABackground.TESTED_PATHWAY_ANNOTATED;
		} else {
			throw new RuntimeException(setting + " is not a possible value for this enum.");
		}
	}
}

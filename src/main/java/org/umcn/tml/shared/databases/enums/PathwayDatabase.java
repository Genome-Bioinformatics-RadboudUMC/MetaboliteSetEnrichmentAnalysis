package org.umcn.tml.shared.databases.enums;

public enum PathwayDatabase {
	ALL("ALL"),
	KEGG("KEGG", "(hsa|map)[0-9]{5}"),
	SMPDB("SMPDB", "smp[0-9]{7}");
	
	private static final String ANNOTATED = "pAnnotated";
	
	private final String name;
	private final String idFormat;
	
	PathwayDatabase(String name) {
		this.name = name;
		this.idFormat = null;
	}
	
	PathwayDatabase(String name, String idFormat) {
		this.name = name;
		this.idFormat = idFormat;
	}
	
	public String getName() {
		return this.name;
	}

	public String getFilterTitle() {
		switch(this) {
		case ALL:
			return ANNOTATED+"_";
		default:
			return ANNOTATED + this.name + "_";
		}
	}
	
	public String getIDFormat() {
		return this.idFormat;
	}
	
	public static PathwayDatabase getPathwayDatabase(String pathwayID) {
		pathwayID = pathwayID.toLowerCase();
		
		for (PathwayDatabase pd : PathwayDatabase.values()) {
			if (pd.equals(PathwayDatabase.ALL)) {
				//skip
			} else if (pathwayID.matches(pd.idFormat)) {
				return pd;
			}
		}		
		return null;
	}
	
	public static boolean isPathwayID(String pathwayID, PathwayDatabase pd) {
		pathwayID = pathwayID.toLowerCase();
		if (pd.equals(ALL)) {
			for (PathwayDatabase allPD : PathwayDatabase.values()) {
				if (allPD.equals(PathwayDatabase.ALL)) {
					//skip
				} else if (pathwayID.matches(allPD.idFormat)) {
					return true;
				}
			}
		} else if (pathwayID.matches(pd.idFormat)) {
			return true;
		}		
		return false;
	}
}

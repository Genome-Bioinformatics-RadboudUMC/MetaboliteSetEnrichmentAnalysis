package org.umcn.tml.msea.datafiles.enums;

import org.umcn.tml.msea.datafiles.headers.labels.MSEAClusterLabel;
import org.umcn.tml.msea.datafiles.headers.labels.MSEAMetaboliteSetLabel;

public enum MSEAStatisticalFilterLevel {
	No_Statistical_Filter("", 
			null,
			null),
	Raw("Raw", 
			MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value,
			MSEAClusterLabel.Hypergeometric_Test_P_Value),
	FDR("FDR", 
			MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value_Benjamini_Hochberg,
			MSEAClusterLabel.Hypergeometric_Test_P_Value_Benjamini_Hochberg),
	BH("BH", 
			MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value_Bonferoni_Holm,
			MSEAClusterLabel.Hypergeometric_Test_P_Value_Bonferoni_Holm);
	
	private final String name;
	private final MSEAMetaboliteSetLabel mseaMetaboliteSetLabel;
	private final MSEAClusterLabel mseaClusterLabel;
	
	MSEAStatisticalFilterLevel(String name, MSEAMetaboliteSetLabel mseaMetaboliteSetLabel, MSEAClusterLabel mseaClusterLabel) {
		this.name = name;
		this.mseaMetaboliteSetLabel = mseaMetaboliteSetLabel;
		this.mseaClusterLabel = mseaClusterLabel;
	}
	
	public MSEAMetaboliteSetLabel getMSEAMetaboliteSetLabel() {
		return this.mseaMetaboliteSetLabel;
	}
	
	public MSEAClusterLabel getMSEAClusterLabel() {
		return this.mseaClusterLabel;
	}
	
	public String getName() {
		return this.name;
	}

	public String getFilterTitle() {
		switch(this) {
		case No_Statistical_Filter:
			return "";
		default:
			return this.name + "_";
		}
	}
	
}

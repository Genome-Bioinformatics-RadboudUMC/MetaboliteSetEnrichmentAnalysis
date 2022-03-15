package org.umcn.tml.msea.datafiles.headers.labels;

import java.util.ArrayList;
import java.util.List;

import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

@SuppressWarnings("rawtypes")
public enum MSEAMetaboliteSetLabel implements Label {
	Metabolite_Set_ID("Metabolite_Set_ID", new String[]{}, String.class),
	Metabolite_Set_Name("Metabolite_Set_Name", new String[]{}, String.class),
	Metabolite_Set_Category("Metabolite_Set_Category", new String[]{}, String.class),
	Metabolite_Set_Metabolites("Metabolite_Set_Metabolites", new String[]{}, String.class),
	Metabolite_Set_Genes("Metabolite_Set_Genes", new String[]{}, String.class),	
	Associated_Metabolite_Set("Associated_Metabolite_Set", new String[]{}, String.class),
	Aberrant_Features("Aberrant_Features", new String[]{}, String.class),
	Normal_Features("Normal_Features", new String[]{}, String.class),
	Aberrant_Metabolites("Aberrant_Metabolites", new String[]{}, String.class),
	Normal_Metabolites("Normal_Metabolites", new String[]{}, String.class),
	Highest_Feature_FC_Increase("Highest_Feature_FC_Increase", new String[]{}, Double.class),
	Highest_Feature_FC_Decrease("Highest_Feature_FC_Decrease", new String[]{}, Double.class),
	Feature_FC_NaNs("Feature_FC_NaNs", new String[]{}, Integer.class),
	Hypergeometric_Test_n11("Hypergeometric_Test_n11", new String[]{}, Integer.class),
	Hypergeometric_Test_n12("Hypergeometric_Test_n12", new String[]{}, Integer.class),
	Hypergeometric_Test_n21("Hypergeometric_Test_n21", new String[]{}, Integer.class),
	Hypergeometric_Test_n22("Hypergeometric_Test_n22", new String[]{}, Integer.class),
	Hypergeometric_Test_P_Value("Hypergeometric_Test_P-Value", new String[]{}, Double.class),
	Hypergeometric_Test_P_Value_Benjamini_Hochberg("Hypergeometric_Test_P-Value_Benjamini_Hochberg", new String[]{}, Double.class),
	Hypergeometric_Test_P_Value_Bonferoni_Holm("Hypergeometric_Test_P-Value_Bonferoni_Holm", new String[]{}, Double.class),
	Visualization_Hyperlink("Visualization_Hyperlink", new String[]{}, String.class),
	Cluster_ID("Cluster_ID", new String[] {}, String.class);
	
	
	private final String label;
	private final String[] synonyms;
	private final Class valueClass;

	MSEAMetaboliteSetLabel(String label, String[] synonyms, Class valueClass) {
		this.label = label;
		this.synonyms = synonyms;
		this.valueClass = valueClass;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String[] getSynonyms() {
		return synonyms;
	}

	@Override
	public Class getValueClass() {
		return valueClass;
	}

	@Override
	public boolean equals(String name) {
		return name.equals(label);
	}

	@Override
	public boolean isSynonym(String name) {
		for (String synonym: getSynonyms()) {
			if (name.equals(synonym)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return label;
	}

	public static List<String> labels() {
		List<String> labels = new ArrayList<String>();
		for (MSEAMetaboliteSetLabel value: values()) {
			labels.add(value.getLabel());
		}
		return labels;
	}
}

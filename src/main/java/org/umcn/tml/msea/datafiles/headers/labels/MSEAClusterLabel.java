package org.umcn.tml.msea.datafiles.headers.labels;

import java.util.ArrayList;
import java.util.List;

import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

@SuppressWarnings("rawtypes")
public enum MSEAClusterLabel implements Label {
	Cluster_ID("Cluster_ID", new String[]{}, String.class),
	Keywords("Keywords", new String[]{}, String.class),
	Representative_Metabolite_Set_ID("Representative_Metabolite_Set_ID", new String[]{}, String.class),
	Representative_Metabolite_Set_Name("Representative_Metabolite_Set_Name", new String[]{}, String.class),
	Representative_Metabolite_Set_Category("Representative_Metabolite_Set_Category", new String[]{}, String.class),
	Aberrant_Features("Aberrant_Features", new String[]{}, String.class),
	Aberrant_Metabolites("Aberrant_Metabolites", new String[]{}, String.class),
	Normal_Features("Normal_Features", new String[]{}, String.class),
	Normal_Metabolites("Normal_Metabolites", new String[]{}, String.class),
	Highest_FC_Increase("Highest_FC_Increase", new String[]{}, Double.class),
	Highest_FC_Decrease("Highest_FC_Decrease", new String[]{}, Double.class),
	FC_NaNs("FC_NaNs", new String[]{}, Integer.class),
	Hypergeometric_Test_n11("Hypergeometric_Test_n11", new String[]{}, Integer.class),
	Hypergeometric_Test_n12("Hypergeometric_Test_n12", new String[]{}, Integer.class),
	Hypergeometric_Test_n21("Hypergeometric_Test_n21", new String[]{}, Integer.class),
	Hypergeometric_Test_n22("Hypergeometric_Test_n22", new String[]{}, Integer.class),
	Hypergeometric_Test_P_Value("Hypergeometric_Test_P_Value", new String[]{}, Double.class),
	Hypergeometric_Test_P_Value_Benjamini_Hochberg("Hypergeometric_Test_P_Value_Benjamini_Hochberg", new String[]{}, Double.class),
	Hypergeometric_Test_P_Value_Bonferoni_Holm("Hypergeometric_Test_P_Value_Bonferoni_Holm", new String[]{}, Double.class),
	Similar_Clusters("Similar_Clusters", new String[]{}, String.class),
	Metabolite_Set_IDs("Metabolite_Set_IDs", new String[]{}, String.class),
	Metabolite_Set_Names("Metabolite_Set_Names", new String[]{}, String.class),
	Metabolite_Set_Categories("Metabolite_Set_Categories", new String[]{}, String.class);
	
	
	private final String label;
	private final String[] synonyms;
	private final Class valueClass;

	MSEAClusterLabel(String label, String[] synonyms, Class valueClass) {
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
		for (MSEAClusterLabel value: values()) {
			labels.add(value.getLabel());
		}
		return labels;
	}
}

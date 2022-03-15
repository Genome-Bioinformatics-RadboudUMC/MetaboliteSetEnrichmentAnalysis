package org.umcn.tml.msea.datafiles.headers.labels;

import java.util.ArrayList;
import java.util.List;

import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

@SuppressWarnings("rawtypes")
public enum FeatureTableLabel implements Label {
	Feature_ID("Feature_ID", new String[]{"F"}, String.class),
	HMP("HMP", new String[]{}, String.class),
	KEGG_Entry("KEGG_Entry", new String[]{}, String.class),
	Fold_Change("Fold_Change", new String[]{}, Double.class),
	P_Value("P-Value", new String[]{"Benjamini-Hochberg_P-Value_Final"}, Double.class),
	MSEA_Metabolite_Set_ID("MSEA_Metabolite_Set_ID", new String[]{}, String.class),
	MSEA_Cluster_ID("MSEA_Cluster_ID", new String[]{}, String.class);
	
	
	private final String label;
	private final String[] synonyms;
	private final Class valueClass;

	FeatureTableLabel(String label, String[] synonyms, Class valueClass) {
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
		List<String> labels = new ArrayList<>();
		for (FeatureTableLabel value: values()) {
			labels.add(value.getLabel());
		}
		return labels;
	}
}

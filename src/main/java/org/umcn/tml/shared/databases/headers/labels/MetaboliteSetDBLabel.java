package org.umcn.tml.shared.databases.headers.labels;

import java.util.ArrayList;
import java.util.List;

import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

@SuppressWarnings("rawtypes")
public enum MetaboliteSetDBLabel implements Label {
	Database("Database", new String[]{}, String.class),
	ID("ID", new String[]{}, String.class),
	Name("Name", new String[]{}, String.class),
	Category("Category", new String[]{}, String.class),
	Genes("Genes", new String[]{}, String.class),
	Metabolites("Metabolites", new String[]{}, String.class),
	Associated_Metabolite_Sets("Associated_Metabolite_Sets", new String[]{}, String.class);
	
	private final String label;
	private final String[] synonyms;
	private final Class valueClass;

	MetaboliteSetDBLabel(String label, String[] synonyms, Class valueClass) {
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
		for (MetaboliteSetDBLabel value: values()) {
			labels.add(value.getLabel());
		}
		return labels;
	}
}

package org.umcn.tml.shared.databases.headers.labels;

import java.util.ArrayList;
import java.util.List;

import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

@SuppressWarnings("rawtypes")
public enum SMPDBMetaboliteIDLabel implements Label {
	SMPDB_ID("SMPDB_ID", new String[]{}, String.class),
	HMDB_ID("HMDB_ID", new String[]{}, String.class),
	KEGG_ID("KEGG_ID", new String[]{}, String.class),
	Chebi_ID("Chebi_ID", new String[]{}, String.class),
	Drugbank_ID("Drugbank_ID", new String[]{}, String.class);

	private final String label;
	private final String[] synonyms;
	private final Class valueClass;

	SMPDBMetaboliteIDLabel(String label, String[] synonyms, Class valueClass) {
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
		for (SMPDBMetaboliteIDLabel value: values()) {
			labels.add(value.getLabel());
		}
		return labels;
	}
}

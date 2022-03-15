package org.umcn.tml.shared.datastructures.datatable.textfile.enums;

public enum FieldSeparator {
	TAB("\t"),
	COMMA(","),
	NA(null);
	
	String fieldSeparator;
	
	private FieldSeparator(String fieldSeparator) {
		this.fieldSeparator = fieldSeparator;
	}

	public String getFieldSeparator() {
		return fieldSeparator;
	}
}

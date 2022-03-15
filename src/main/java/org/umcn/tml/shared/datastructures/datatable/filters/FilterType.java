package org.umcn.tml.shared.datastructures.datatable.filters;

import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

@SuppressWarnings("rawtypes")
public enum FilterType {
	//TODO: DoesNotEqual!
	EQUALS(new Class[]{String.class, Double.class, Integer.class, String[].class}),
	DOES_NOT_EQUAL(new Class[]{String.class, Double.class, Integer.class, String[].class}),
	CONTAINS(new Class[]{String.class, String[].class}),
	DOES_NOT_CONTAIN(new Class[]{String.class, String[].class}),
	MATCHES(new Class[]{String.class, String[].class}),
	GREATER_THAN(new Class[]{Double.class, Integer.class}),
	GREATER_THAN_OR_EQUAL_TO(new Class[]{Double.class, Integer.class}),
	LESS_THAN(new Class[]{Double.class, Integer.class}),
	LESS_THAN_OR_EQUAL_TO(new Class[]{Double.class, Integer.class});
	
	Class[] valueClassList;
	
	private FilterType(Class[] valueClassList) {
		this.valueClassList = valueClassList;
	}
	
	public boolean labelHasMatchingValueClass(Label label) {
		for (Class valueClass : valueClassList) {
			if (label.getValueClass().equals(valueClass)) return true;
		}
		return false;
	}
}

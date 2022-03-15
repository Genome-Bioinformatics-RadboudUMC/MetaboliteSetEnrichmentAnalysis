package org.umcn.tml.shared.datastructures.datatable.filters;

import java.util.InputMismatchException;

import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

public class Filter<T> {
	final private Label label;
	final private FilterType filterType;	
	final private T[] valueArray;
	private boolean nullValue;
	
	//By default these are unimplemented...
	private boolean nanValue;
	private boolean positiveInfinityValue;
	private boolean negativeInfinityValue;
	
	public Filter(Label label, FilterType filterType, boolean nullValue, T[] value) {
		this.label = label;
		this.filterType = filterType;
		this.valueArray = value;
		this.nullValue = nullValue;
		
		@SuppressWarnings("rawtypes")
		Class valueArrayClass = value.getClass();
		@SuppressWarnings("rawtypes")
		Class valueElementClass = value[0].getClass(); //TODO: What if value is empty?
		if (label.getValueClass().isArray() && !label.getValueClass().equals(valueArrayClass)) {
			throw new InputMismatchException(label.getLabel() + " column contains values of type: " + label.getValueClass().getName() + ". But the filter is of type: " + valueArrayClass.getName());
		} else if (!label.getValueClass().isArray() && !label.getValueClass().equals(valueElementClass)) {			
			throw new InputMismatchException(label.getLabel() + " column contains values of type: " + label.getValueClass().getName() + ". But the filter is of type: " + valueElementClass.getName());
		} else if (!filterType.labelHasMatchingValueClass(label)) {
			throw new InputMismatchException("Filter type \"" + filterType.toString() + "\" does not work for " + label.getValueClass().getName() + " values.");
		}
			
	}

	public Label getLabel() {
		return label;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public Boolean isNullValue() {
		return nullValue;
	}
	
	public Filter<T> setNullValue(boolean nullValue) {
		this.nullValue = nullValue;
		return this;
	}

	public Boolean isNanValue() {
		return nanValue;
	}
	
	public Filter<T> setNanValue(boolean nanValue) {
		this.nanValue = nanValue;
		return this;
	}

	public Boolean isPositiveInfinityValue() {
		return positiveInfinityValue;
	}
	
	public Filter<T> setPositiveInfinityValue(boolean positiveInfinityValue) {
		this.positiveInfinityValue = positiveInfinityValue;
		return this;
	}

	public Boolean isNegativeInfinityValue() {
		return negativeInfinityValue;
	}
	
	public Filter<T> setNegativeInfinityValueValue(boolean negativeInfinityValue) {
		this.negativeInfinityValue = negativeInfinityValue;
		return this;
	}


	public T[] getValueArray() {
		return valueArray;
	}
}

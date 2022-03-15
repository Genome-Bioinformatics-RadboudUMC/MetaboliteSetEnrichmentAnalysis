package org.umcn.tml.shared.datastructures.datatable;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

public class DataTableRow {
	
	private final static Logger logger = LoggerFactory.getLogger(DataTableRow.class);
	
	private final Set<Label> headerSet;
	private final Map<Label, String> stringFields;
	private final Map<Label, Double> doubleFields;
	private final Map<Label, Integer> integerFields;	
	private String originalRow;
	
	//TODO: use more memory efficient map/data type
	public DataTableRow() {
		this.headerSet = new LinkedHashSet<>();		
		this.stringFields = new HashMap<>();
		this.doubleFields = new HashMap<>();
		this.integerFields = new HashMap<>();
		this.originalRow = "";
	}
	
	public DataTableRow(String originalRow) {
		this.headerSet = new LinkedHashSet<>();		
		this.stringFields = new HashMap<>();
		this.doubleFields = new HashMap<>();
		this.integerFields = new HashMap<>();
		this.originalRow = originalRow;
	}
	
	public DataTableRow(DataTableRow dataTableRow) {
		this(dataTableRow.getOriginalRow());
		this.headerSet.addAll(dataTableRow.headerSet);
		this.stringFields.putAll(dataTableRow.stringFields);
		this.doubleFields.putAll(dataTableRow.doubleFields);
		this.integerFields.putAll(dataTableRow.integerFields);
	}
	
	public DataTableRow(String originalRow, DataTableRow...dataTableRow) {
		this(originalRow);
		for (DataTableRow dtr : dataTableRow) {
			this.headerSet.addAll(dtr.headerSet);
			this.stringFields.putAll(dtr.stringFields);
			this.doubleFields.putAll(dtr.doubleFields);
			this.integerFields.putAll(dtr.integerFields);
		}
	}
	
	public boolean addField(Label label, Double value, boolean replace) {
		if (headerSet.contains(label) && !replace) {
			return false;
		} else if (label.getValueClass().equals(Double.class)) {
			if ((!replace) && doubleFields.get(label) != null) return false;
			headerSet.add(label);
			doubleFields.put(label, value);
			return true;			
		} else if (label.getValueClass().equals(Integer.class)) {
			if ((!replace) && integerFields.get(label) != null) return false;
			headerSet.add(label);
			integerFields.put(label, value.intValue());
			return true;			
		} else if (label.getValueClass().equals(String.class)) { 
			if ((!replace) && stringFields.get(label) != null) return false;
			headerSet.add(label);
			stringFields.put(label, Double.toString(value));
			return true;		
		}  else {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" does not have a numeric value class.");
		}
	}
	
	public boolean addField(Label label, Integer value, boolean replace) {
		if (headerSet.contains(label) && !replace) {
			return false;
		} else if (label.getValueClass().equals(Integer.class)) {
			if ((!replace) && integerFields.get(label) != null) return false;
			headerSet.add(label);
			integerFields.put(label, value);
			return true;
		} else if (label.getValueClass().equals(Double.class)) {
			if ((!replace) && doubleFields.get(label) != null) return false;
			headerSet.add(label);
			doubleFields.put(label, value.doubleValue());
			return true;
		} else if (label.getValueClass().equals(String.class)) { 
			if ((!replace) && stringFields.get(label) != null) return false;
			headerSet.add(label);
			stringFields.put(label, Integer.toString(value));
			return true;		
		} else {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" does not have a numeric value class.");
		}
	}
	
	public boolean addField(Label label, String value, boolean replace) throws IOException {	
		if (headerSet.contains(label) && !replace) {
			return false;
		} else if (label.getValueClass() == String.class) {
			if ((!replace) && stringFields.get(label) != null && !stringFields.get(label).equals("")) return false;
			stringFields.put(label, value);
			headerSet.add(label);
			return true;
		} else if (label.getValueClass() == Double.class) {
			if ((!replace) && doubleFields.get(label) != null) return false;
			try {
				if (value.equals("")) {	
					doubleFields.put(label, null);		
				} else if (value.toLowerCase().equals("null")) { 
					doubleFields.put(label, null);
				} else {	
					doubleFields.put(label, Double.parseDouble(value));	
				}
				headerSet.add(label);	
				return true;
			} catch (NumberFormatException e) {
				throw new RuntimeException("Column \"" + label.getLabel() + "\" was expected to contain doubles, but value is: " + value, e);
			}
		} else if (label.getValueClass() == Integer.class) {
			if ((!replace) && integerFields.get(label) != null) return false;
			try {
				if (value.equals("")) {	
					integerFields.put(label, null);						
				} else if (value.toLowerCase().equals("null")) { 
					doubleFields.put(label, null);
				} else {	
					integerFields.put(label, Integer.parseInt(value));			
				}
				headerSet.add(label);	
				return true;
			} catch (NumberFormatException e) {
				throw new RuntimeException("Column \"" + label.getLabel() + "\" was expected to contain integers, but value is: " + value, e);
			}
		} else { 
			throw new RuntimeException("Label \"" + label.getLabel() + "\" has an invalid value class: " + label.getValueClass().getName());
		}
	}	
	
	public void removeField(Label label) {
		headerSet.remove(label);
		
		if (label.getValueClass().equals(Double.class)) {
			this.doubleFields.remove(label);
		} else if (label.getValueClass().equals(Integer.class)) {
			this.integerFields.remove(label);
		} else if (label.getValueClass().equals(String.class)) {
			this.stringFields.remove(label);
		}		
	}
	
	public boolean appendStringField(Label label, String string, String separator) {
		if (label.getValueClass().equals(String.class)) {
			String old = this.stringFields.get(label);
			if (old.equals("") || old == null) {
				this.stringFields.put(label, string);
			} else {
				this.stringFields.put(label, old + separator + string);				
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void synchroniseFieldsWithHeader(Set<Label> newHeaderSet) throws IOException {
		addMissingFields(newHeaderSet);
		deleteExcessiveFields(newHeaderSet);
	}
	
	public Set<Label> getHeaderSet() {
		return Collections.unmodifiableSet(headerSet);
	}
	
	public Double getDouble(Label label) {
		if (label.getValueClass().equals(Double.class)) {
			return doubleFields.get(label);		
		} else {
			throw new InputMismatchException("The \"" + label.getLabel() + "\" column does not contain Doubles.");
		}
	}
	
	public Integer getInteger(Label label) {
		if (label.getValueClass().equals(Integer.class)) {
			return integerFields.get(label);		
		} else {
			throw new InputMismatchException("The \"" + label.getLabel() + "\" column does not contain Integers.");
		}
	}
	
	//TODO: shouldn't this always work? you can easily return numbers as strings...
	public String getString(Label label) {
		if (label.getValueClass().equals(String.class)) {
			return stringFields.get(label);
		} else {
			throw new InputMismatchException("The \"" + label.getLabel() + "\" column does not contain Strings.");
		}
	}
	
	public String getAnyFieldAsString(Label label) {
		if (label.getValueClass().equals(Double.class)) {
			return doubleFields.get(label).toString();
		} else if (label.getValueClass().equals(Integer.class)) {
			return integerFields.get(label).toString();
		} else if (label.getValueClass().equals(String.class)) {
			return stringFields.get(label);
		} else {
			throw new InputMismatchException("The \"" + label.getLabel() + "\" column does not contain Strings.");
		}
	}
	
	public Set<Label> getDoubleKeySet() {
		return doubleFields.keySet();
	}
	
	public Set<Label> getIntegerKeySet() {
		return integerFields.keySet();
	}
	
	public Set<Label> getStringKeySet() {
		return stringFields.keySet();
	}
	
	public Collection<Double> getDoubleValues() {
		return doubleFields.values();
	}
	
	public Collection<Integer> getIntegerValues() {
		return integerFields.values();
	}
	
	public Collection<String> getStringValues() {
		return stringFields.values();
	}
	
	protected void addMissingFields(Set<Label> newHeaderSet) throws IOException {
		for (Label label : newHeaderSet) {
			if (!headerSet.contains(label)) {
				addField(label, "", false);
			}
		}
	}
	
	protected void deleteExcessiveFields(Set<Label> newHeaderSet) {
		for (Label label : headerSet) {
			if (!newHeaderSet.contains(label)) {
				removeField(label);
			}
		}
	}
	
	public String getOriginalRow() {
		return originalRow;
	}
	
	public void replaceFieldInOriginalRow(int index, String originalText, String newText) {
		String[] splitOriginalRow = originalRow.split("\t");
		splitOriginalRow[index] = splitOriginalRow[index].replace(originalText, newText);
		
		originalRow = String.join("\t", splitOriginalRow);	
	}
}
package org.umcn.tml.shared.datastructures.datatable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.umcn.tml.shared.datastructures.SortMapOnValues;
import org.umcn.tml.shared.datastructures.SortMapOnValues.Order;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

//TODO: can I modify the elements in an immutable list?
//TODO: you can alter a DataTableRow without updating the DataTable it is stored in (via DataTableRow's addField or removefield).
public class DataTable {
	
	private final static Logger logger = LoggerFactory.getLogger(DataTable.class);
	
	private final String fileName;
	private final Set<Label> headerSet;
	private final List<DataTableRow> dataTableRowList;
	private final String originalHeader;
	
	public DataTable(String fileName) {
		this.fileName = fileName;
		this.headerSet = new LinkedHashSet<>();
		this.dataTableRowList = new ArrayList<>();	
		this.originalHeader = "";		
	}
	
	public DataTable(String fileName, String originalHeader) {
		this.fileName = fileName;
		this.headerSet = new LinkedHashSet<>();
		this.dataTableRowList = new ArrayList<>();	
		this.originalHeader = originalHeader;
	}
	
	public DataTable(String fileName, Set<Label> headerSet, String originalHeader) {
		this(fileName, originalHeader);		
		this.headerSet.addAll(headerSet);
	}
	
	public DataTable(String fileName, List<Label> headerList, String originalHeader) {
		this(fileName, originalHeader);	
		
		this.headerSet.addAll(headerList);
		if (headerList.size() != headerSet.size()) {
			//For example, removeColumn(label), only removes the first...
			throw new RuntimeException("Header of Text File \" " + fileName + "\" contains duplicate column titles and the current DataTable structure is not setup to handle this properly.");
		}
	}
	
	public DataTable(String fileName, Label[] headerArray, String originalHeader) {
		this(fileName, Arrays.asList(headerArray), originalHeader);
	}
	
	public DataTable(boolean immutable, DataTable dataTable) throws IOException {
		this(dataTable.getFileName(), dataTable.getOriginalHeader());		
		add(immutable, dataTable);
	}
	
	public DataTable(boolean immutable, String fileName, String originalHeader, DataTable... dataTableArray) throws IOException {
		this(fileName, originalHeader);		
		add(immutable, dataTableArray);
	}
	
	public void add(boolean immutable, DataTable... dataTableArray) throws IOException {
		for (DataTable dataTable : dataTableArray) {
			add(immutable, dataTable);			
		}		
	}
	
	public void add(boolean immutable, DataTable dataTable) throws IOException {
		dataTable.addColumns(headerSet);
		addColumns(dataTable.getHeaderSet());
		
		if (immutable) {
			for (DataTableRow dataTableRow : dataTable.getRowList()) {
				addRow(immutable, dataTableRow);
			}
		} else {
			this.dataTableRowList.addAll(dataTable.getRowList());	//faster, in case immutable is not needed.
		}
	}
	
	public void addRow(boolean immutable, DataTableRow dataTableRow) throws IOException {
		if (immutable) {
			dataTableRow = new DataTableRow(dataTableRow);
		}
		
		dataTableRow.addMissingFields(headerSet);
		addColumns(dataTableRow.getHeaderSet());
		
		dataTableRowList.add(dataTableRow);
	}
	
	public void addColumns(Set<Label> labelSet) throws IOException {
		for (Label label : labelSet) {
			addColumn(label);
		}
	}
	
	public void addColumns(Label...labels) throws IOException {
		for (Label label : labels) {
			addColumn(label);
		}
	}
	
	public void addColumn(Label label) throws IOException {
		addColumn(label, "");
	}
	
	//TODO: make NA for Strings? NaN for Numbers?
	public void addColumn(Label label, String defaultValue) throws IOException {
		if (!headerSet.contains(label)) {
			headerSet.add(label);
			for (DataTableRow dataTableRow : dataTableRowList) {
				dataTableRow.addField(label, defaultValue, false);
			}				
		}
	}
	
	public void removeColumns(Set<Label> labelSet) {
		for (Label label : labelSet) {
			removeColumn(label);
		}
	}
	
	public void removeColumns(Label...labels) {
		for (Label label : labels) {
			removeColumn(label);
		}
	}
	
	public void removeColumn(Label label) {
		if (headerSet.contains(label)) {
			headerSet.remove(label);
			for (DataTableRow dataTableRow : dataTableRowList) {
				dataTableRow.removeField(label);
			}
		}
	}
	
	public void clearColumns(Label...labels) throws IOException {
		for (Label label : labels) {
			clearColumn(label);
		}
	}
	
	public void clearColumn(Label label) throws IOException {
		if (headerSet.contains(label)) {
			for (DataTableRow dataTableRow : dataTableRowList) {
				dataTableRow.addField(label, "", true);
			}				
		}
	}
	
	public void synchroniseColumnsWithHeader(Set<Label> newHeaderSet) throws IOException {
		addColumns(newHeaderSet);
		removeColumns(newHeaderSet);		
	}
	
	public String getFileName() {
		return fileName;
	}

	public List<DataTableRow> getRowList() {		
		return Collections.unmodifiableList(dataTableRowList); //TODO: objects in list can still be modified.
	}
	
	public DataTableRow getFirstRow(Label label, Set<String> stringSet) {
		return getFirstRow(label, stringSet.toArray(new String[stringSet.size()]));
	}
		
	public DataTableRow getFirstRow(Label label, String...stringArray) {
			for (DataTableRow dataTableRow : dataTableRowList) {
			for (String string : stringArray) {
				if (dataTableRow.getAnyFieldAsString(label).equals(string)) {
					return dataTableRow;
				}
			}
		}	
		return null;
	}
	
	public int getFirstRowIndex(Label label, Set<String> stringSet) {
		return getFirstRowIndex(label, stringSet.toArray(new String[stringSet.size()]));
	}
	public int getFirstRowIndex(Label label, String...stringArray) {
		for (int i = 0; i < dataTableRowList.size(); i++) {		
			DataTableRow dataTableRow = dataTableRowList.get(i);
			for (String string : stringArray) {
				if (dataTableRow.getAnyFieldAsString(label).equals(string)) {
					return i;
				}				
			}
		}
		
		return -1;
	}

	public Set<Label> getHeaderSet() {
		return Collections.unmodifiableSet(headerSet);
	}
	
	public int size() {
		return dataTableRowList.size();
	}
	
	public boolean isEmpty() {
		return dataTableRowList.isEmpty();
	}
	
	public boolean contains(Label label, Double value) {
		if (!label.getValueClass().equals(Double.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Doubles.");
		}
		
		for (DataTableRow dataTableRow : dataTableRowList) {
			if (value.equals(dataTableRow.getDouble(label))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(Label label, Integer value) {
		if (!label.getValueClass().equals(Integer.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Integers.");
		}
		
		for (DataTableRow dataTableRow : dataTableRowList) {
			if (value.equals(dataTableRow.getInteger(label))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(Label label, String value, boolean equals) {
		if (!label.getValueClass().equals(String.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Strings.");
		}
		
		for (DataTableRow dataTableRow : dataTableRowList) {
			if (equals) {
				if (dataTableRow.getString(label).equals(value)) {
					return true;
				} 
			} else {
				if (dataTableRow.getString(label).contains(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public DataTableRow getRowWithHighestValue(Label label) {
		Class valueClass = label.getValueClass();
		
		if (!valueClass.equals(Double.class) && !valueClass.equals(Integer.class)) {
			throw new InputMismatchException("The \"" + label.getLabel() + "\" column does not contain Doubles or Integers.");
		}
		
		DataTableRow dataTableRowMax = null;
		for (DataTableRow dataTableRowCurrent : dataTableRowList) {
			if (dataTableRowMax == null) {
				dataTableRowMax = dataTableRowCurrent;
			} else if (valueClass.equals(Double.class) 
						&& dataTableRowCurrent.getDouble(label) != null
						&& dataTableRowMax.getDouble(label) < dataTableRowCurrent.getDouble(label)) {
				dataTableRowMax = dataTableRowCurrent;
			} else if (valueClass.equals(Integer.class) 
						&& dataTableRowCurrent.getInteger(label) != null 
						&& dataTableRowMax.getInteger(label) < dataTableRowCurrent.getInteger(label)) {
				dataTableRowMax = dataTableRowCurrent;
			}
		}
		return dataTableRowMax;
	}
	
	public double getHighestDoubleValue(Label label, boolean includeNaN) {
		double max = 0.0;
		for (DataTableRow dataTableRow : dataTableRowList) {
			Double current = dataTableRow.getDouble(label);
			if (current == null) continue;
			if (current.isNaN() && !includeNaN) continue;
			if (max < current) {
				max = current;
				
			}
		}
		return max;
	}
	
	public int getHighestIntegerValue(Label label) {
		int max = 0;
		for (DataTableRow dataTableRow : dataTableRowList) {
			Integer current = dataTableRow.getInteger(label);
			if (current == null) continue;
			if (max < current) {
				max = current;
			}
		}
		return max;
	}
	
	public DataTableRow getRowWithLowestValue(Label label) {
		Class valueClass = label.getValueClass();
		
		if (!valueClass.equals(Double.class) && !valueClass.equals(Integer.class)) {
			throw new InputMismatchException("The \"" + label.getLabel() + "\" column does not contain Doubles or Integers.");
		}
		
		DataTableRow dataTableRowMin = null;
		for (DataTableRow dataTableRowCurrent : dataTableRowList) {
			if (dataTableRowMin == null) {
				dataTableRowMin = dataTableRowCurrent;
			} else if (valueClass.equals(Double.class) 
						&& dataTableRowCurrent.getDouble(label) != null 
						&& dataTableRowMin.getDouble(label) > dataTableRowCurrent.getDouble(label)) {
				dataTableRowMin = dataTableRowCurrent;
			} else if (valueClass.equals(Integer.class) 
						&& dataTableRowCurrent.getInteger(label) != null 
						&& dataTableRowMin.getInteger(label) > dataTableRowCurrent.getInteger(label)) {
				dataTableRowMin = dataTableRowCurrent;
			}
		}
		return dataTableRowMin;
	}
	
	public double getLowestDoubleValue(Label label, boolean includeNaN) {
		double min = 0.0;
		for (DataTableRow dataTableRow : dataTableRowList) {
			Double current = dataTableRow.getDouble(label);
			if (current == null) continue;
			if (current.isNaN() && !includeNaN) continue;
			if (min > current) {
				min = current;
			}
		}
		return min;
	}
	
	public int getLowestIntegerValue(Label label) {
		int min = 0;
		for (DataTableRow dataTableRow : dataTableRowList) {
			Integer current = dataTableRow.getInteger(label);
			if (current == null) continue;
			if (min > current) {
				min = current;
			}
		}
		return min;
	}
	
	public int getOccurence(Label label, Double value) {
		if (!label.getValueClass().equals(Double.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Doubles.");
		}
		
		int occurence = 0;
		for (DataTableRow dataTableRow : dataTableRowList) {
			if (value.equals(dataTableRow.getDouble(label))) {
				occurence++;
			}
		}
		return occurence;
	}
	
	public int getOccurence(Label label, Integer value) {
		if (!label.getValueClass().equals(Integer.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Integers.");
		}

		int occurence = 0;
		for (DataTableRow dataTableRow : dataTableRowList) {
			if (value.equals(dataTableRow.getInteger(label))) {
				occurence++;
			}
		}
		return occurence;
	}
	
	public int getOccurence(Label label, String value, boolean equals) {
		if (!label.getValueClass().equals(String.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Strings.");
		}

		int occurence = 0;
		for (DataTableRow dataTableRow : dataTableRowList) {			
			if (equals && value.equals(dataTableRow.getString(label))) {
				occurence++;
			} else if ((!equals) && value.contains(dataTableRow.getString(label))) {
				occurence++;
			}
		}
		return occurence;
	}
	
	public Map<String, Integer> getStringColumnOccurenceMap(Label label) {		
		Map<String, Integer> valueOccurenceCount = new LinkedHashMap<>();
	
		for (DataTableRow dataTableRowMetaboliteAnnotations : dataTableRowList) {
			String field = dataTableRowMetaboliteAnnotations.getString(label);
			
			int previousOccurenceCount;
			if (valueOccurenceCount.containsKey(field)) {
				previousOccurenceCount = valueOccurenceCount.get(field);
			} else {
				previousOccurenceCount = 0;
			}
			valueOccurenceCount.put(field, previousOccurenceCount+1);
		}
		
		return valueOccurenceCount;
	}
	
	public Double[] getDoubleColumnAsArray(Label label) {
		if (!label.getValueClass().equals(Double.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Doubles.");
		}
		
    	Double[] featureArray = new Double[dataTableRowList.size()];
    	
    	for (int i = 0; i < dataTableRowList.size(); i++) {
    		double value = dataTableRowList.get(i).getDouble(label);
    		featureArray[i] = value;
    	}
    	
    	return featureArray;
	}
	
	public List<Double> getDoubleColumnAsList(Label label) {
		return Arrays.asList(getDoubleColumnAsArray(label));
	}
	
	public Integer[] getIntegerColumnAsArray(Label label) {
		if (!label.getValueClass().equals(Integer.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Integers.");
		}
		
    	Integer[] featureArray = new Integer[dataTableRowList.size()];
    	
    	for (int i = 0; i < dataTableRowList.size(); i++) {
    		int value = dataTableRowList.get(i).getInteger(label);
    		featureArray[i] = value;
    	}
    	
    	return featureArray;
	}
	
	public List<Integer> getIntegerColumnAsList(Label label) {
		return Arrays.asList(getIntegerColumnAsArray(label));
	}
	
	//TODO: how to handle null values?
	public Set<Integer> getIntegerColumnAsSet(Label label) {
		if (!label.getValueClass().equals(Integer.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Integers.");
		}
		
		Set<Integer> featureSet = new LinkedHashSet<>();
		
		for (DataTableRow row : dataTableRowList) {
    		int value = row.getInteger(label);
    		featureSet.add(value);
		}
    	
    	return featureSet;
	}
	
	public String[] getStringColumnAsArray(Label label) {
		if (!label.getValueClass().equals(String.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Strings.");
		}
		
    	String[] featureArray = new String[dataTableRowList.size()];
    	
    	for (int i = 0; i < dataTableRowList.size(); i++) {
    		String value = dataTableRowList.get(i).getString(label);
    		featureArray[i] = value;
    	}
    	
    	return featureArray;
	}
	
	public Set<String> getStringColumnAsSet(Label label) {
		if (!label.getValueClass().equals(String.class)) {
			throw new RuntimeException("Column \"" + label.getLabel() + "\" contains " + label.getValueClass().getName() + ", but is now treated as a column with Strings.");
		}
		
    	Set<String> featureSet = new HashSet<String>();
    	
    	for (DataTableRow row : dataTableRowList) {
    		String value = row.getString(label);
    		featureSet.add(value);
    	}
    	
    	return featureSet;
	}
	
	public String getColumnAsString(Label label) {
		String columnString = "";
		
		for (DataTableRow row : dataTableRowList) {
			if (label.getValueClass().equals(String.class)) {
				columnString = columnString + ";" + row.getString(label);
			} else if (label.getValueClass().equals(Integer.class)) {
				columnString = columnString + ";" + row.getInteger(label).toString();
			} else if (label.getValueClass().equals(Double.class)) {
				columnString = columnString + ";" + row.getDouble(label).toString();
			}
		}
		
		return columnString.replaceFirst(";", "");
	}

	public String getOriginalHeader() {
		return originalHeader;
	}
	
	public DataTable getSortedDataTable(Label label, Order order, boolean immutable) throws IOException {		
		DataTable sortedDataTable = null;
		if (label.getValueClass().equals(Double.class)) {
			Map<DataTableRow, Double> dataTableRowMap = new LinkedHashMap<>();
			for (DataTableRow dataTableRow : dataTableRowList) {
				double sortingFieldValue = dataTableRow.getDouble(label);
				dataTableRowMap.put(dataTableRow, sortingFieldValue);
			}				
			dataTableRowMap = SortMapOnValues.sortMapByValues(dataTableRowMap, order);
			sortedDataTable = getSortedDataTable(dataTableRowMap, immutable);
		} else if (label.getValueClass().equals(Integer.class)) {
			Map<DataTableRow, Integer> dataTableRowMap = new LinkedHashMap<>();			
			for (DataTableRow dataTableRow : dataTableRowList) {
				int sortingFieldValue = dataTableRow.getInteger(label);
				dataTableRowMap.put(dataTableRow, sortingFieldValue);
			}			
			dataTableRowMap = SortMapOnValues.sortMapByValues(dataTableRowMap, order);
			sortedDataTable = getSortedDataTable(dataTableRowMap, immutable);
		} else if (label.getValueClass().equals(String.class)) {
			Map<DataTableRow, String> dataTableRowMap = new LinkedHashMap<>();		
			for (DataTableRow dataTableRow : dataTableRowList) {
				String sortingFieldValue = dataTableRow.getString(label);
				dataTableRowMap.put(dataTableRow, sortingFieldValue);
			}	
			dataTableRowMap = SortMapOnValues.sortMapByValues(dataTableRowMap, order);	
			sortedDataTable = getSortedDataTable(dataTableRowMap, immutable);
		}
		
		return sortedDataTable;
	}
	
	protected <V> DataTable getSortedDataTable(Map<DataTableRow, V> dataTableRowMapSorted, boolean immutable) throws IOException {
		DataTable sortedDataTable = new DataTable(fileName, headerSet, originalHeader);
		
		for (DataTableRow dataTableRow : dataTableRowMapSorted.keySet()) {
			sortedDataTable.addRow(immutable, dataTableRow);
		}
		
		return sortedDataTable;		
	}
}
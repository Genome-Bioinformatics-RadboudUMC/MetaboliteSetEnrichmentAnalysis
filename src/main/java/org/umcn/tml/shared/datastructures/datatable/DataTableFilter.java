package org.umcn.tml.shared.datastructures.datatable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.umcn.tml.shared.datastructures.datatable.DataTableFilter;
import org.umcn.tml.shared.datastructures.datatable.filters.ApplyFiltersDouble;
import org.umcn.tml.shared.datastructures.datatable.filters.ApplyFiltersInteger;
import org.umcn.tml.shared.datastructures.datatable.filters.ApplyFiltersString;
import org.umcn.tml.shared.datastructures.datatable.filters.Filter;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterSet;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

@Component
public class DataTableFilter {
	
	private final static Logger logger = LoggerFactory.getLogger(DataTableFilter.class);
	
	private ApplyFiltersDouble applyFiltersDouble;
	private ApplyFiltersInteger applyFiltersInteger;	
	private ApplyFiltersString applyFiltersString;

	@Autowired
	public DataTableFilter(	ApplyFiltersDouble applyFiltersDouble,
							ApplyFiltersInteger applyFiltersInteger,
							ApplyFiltersString applyFiltersString) {
		this.applyFiltersDouble = applyFiltersDouble;
		this.applyFiltersInteger = applyFiltersInteger;
		this.applyFiltersString = applyFiltersString;
	}
	
	public DataTable filter(boolean immutable, DataTable dataTable, FilterSet...filterSetArray) throws IOException {		
		
//		logger.info("Filtering: " + dataTable.getFileName());
		
		DataTable filteredDataTable = new DataTable(immutable, dataTable);
		
		for (FilterSet filterSet : filterSetArray) {
			filteredDataTable = filter(false, filteredDataTable, filterSet);
		}
		
//		logger.info("	..." + filteredDataTable.size() + " out of " + dataTable.size() + " rows remaining.");
		
		return filteredDataTable;
	}
	
	public DataTable filter(boolean immutable, DataTable dataTable, FilterSet filterSet) throws IOException {	
		validateDataTableContainsFilterColumns(dataTable, filterSet);	
		
		logger.info("Filtering: " + dataTable.getFileName());		
		
		DataTable filteredDataTable = new DataTable(dataTable.getFileName(), dataTable.getHeaderSet(), dataTable.getOriginalHeader());

		for (DataTableRow dataTableRow : dataTable.getRowList()) {
			if (applyFilters(dataTableRow, filterSet)) {
				filteredDataTable.addRow(immutable, dataTableRow);
			}
		}
		
		logger.info("	..." + filteredDataTable.size() + " out of " + dataTable.size() + " rows remaining.");
		
		return filteredDataTable;
	}
	
	protected void validateDataTableContainsFilterColumns(DataTable dataTable, FilterSet filterSet) throws IOException {
		for (Label label : filterSet.getLabelSet()) {
			if (!dataTable.getHeaderSet().contains(label)) throw new IOException("DataTable \"" + dataTable.getFileName() + "\" does not contain \"" + label.toString() + "\" column and cannot be filtered.");
		}
	}
	
	protected Boolean applyFilters(DataTableRow dataTableRow, FilterSet filterSet) {
		for (Filter<Double> filter : filterSet.getDoubleFilters()) {
			if (applyFiltersDouble.applyfilter(dataTableRow.getDouble(filter.getLabel()), filter)) {
				if (filterSet.isOr()) {
					return true;
				}
			} else {
				if (filterSet.isAnd()) {
					return false;
				}
			}			 
		}
		
		for (Filter<Integer> filter : filterSet.getIntegerFilters()) {
			if (applyFiltersInteger.applyfilter(dataTableRow.getInteger(filter.getLabel()), filter)) {
				if (filterSet.isOr()) {
					return true;
				}
			} else {
				if (filterSet.isAnd()) {
					return false;
				}
			}
		}
		
		for (Filter<String> filter : filterSet.getStringFilters()) {
			if (applyFiltersString.applyfilter(dataTableRow.getString(filter.getLabel()), filter)) {
				if (filterSet.isOr()) {
					return true;
				}
			} else {
				if (filterSet.isAnd()) {
					return false;
				}
			}
		}
		
		if (filterSet.isOr()) {
			return false;
		} else {
			return true;
		}
	}
	
	public DataTable getUniqueValueRows(boolean immutable, DataTable dataTable, Label label, boolean getBlanks) throws IOException {
		
		logger.info("Get unique value rows of the '" + label.getLabel() + "' column in " + dataTable.getFileName());
		
		DataTable dataTableUniqueRows = new DataTable(dataTable.getFileName(), dataTable.getHeaderSet(), dataTable.getOriginalHeader());
		
		if (label.getValueClass().equals(Double.class)) {
			Set<Double> set = new HashSet<>();
			for (DataTableRow dataTableRow : dataTable.getRowList()) {
				Double value = dataTableRow.getDouble(label);
				if (value == null && !getBlanks) {
					continue;
				} else if (!set.contains(value)) {
					set.add(value);
					dataTableUniqueRows.addRow(immutable, dataTableRow);
				}
			}
		} else if (label.getValueClass().equals(Integer.class)) {
			Set<Integer> set = new HashSet<>();
			for (DataTableRow dataTableRow : dataTable.getRowList()) {
				Integer value = dataTableRow.getInteger(label);
				if (value == null && !getBlanks) {
					continue;
				} if (!set.contains(value)) {
					set.add(value);
					dataTableUniqueRows.addRow(immutable, dataTableRow);
				}
			}
		} else if (label.getValueClass().equals(String.class)) {
			Set<String> set = new HashSet<>();
			for (DataTableRow dataTableRow : dataTable.getRowList()) {
				String value = dataTableRow.getString(label);
				if (value.equals("") && !getBlanks) {
					continue;
				} if (!set.contains(value)) {
					set.add(value);
					dataTableUniqueRows.addRow(immutable, dataTableRow);
				}
			}
		}
		
		logger.info("	..." + dataTableUniqueRows.size() + " out of " + dataTable.size() + " rows remaining.");
		
		return dataTableUniqueRows;
	}
}

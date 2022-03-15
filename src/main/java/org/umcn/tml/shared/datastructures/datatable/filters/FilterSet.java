package org.umcn.tml.shared.datastructures.datatable.filters;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

public class FilterSet {
	public enum FilterSetType {
		AND,
		OR;
	}
	
	private final Set<Label> labelSet = new LinkedHashSet<>();
	
	private final List<Filter<Double>> doubleFilterList = new ArrayList<>();
	private final List<Filter<Integer>> integerFilterList = new ArrayList<>();
	private final List<Filter<String>> stringFilterList = new ArrayList<>();
	private final List<Filter<String>> stringArrayFilterList = new ArrayList<>();
	
	FilterSetType filterSetType;
	
	public FilterSet(FilterSetType filterSetType) {
		this.filterSetType = filterSetType;
	}
	
	public void addDoubleFilter(Filter<Double> doubleFilter) {
		this.doubleFilterList.add(doubleFilter);
		this.labelSet.add(doubleFilter.getLabel());
	}
	
	public void addIntegerFilter(Filter<Integer> integerFilter) {
		this.integerFilterList.add(integerFilter);
		this.labelSet.add(integerFilter.getLabel());
	}
	
	public void addStringFilter(Filter<String> stringFilter) {
		this.stringFilterList.add(stringFilter);
		this.labelSet.add(stringFilter.getLabel());
	}
	
	public void addStringArrayFilter(Filter<String> stringArrayFilter) {
		this.stringArrayFilterList.add(stringArrayFilter);
		this.labelSet.add(stringArrayFilter.getLabel());
	}
	
	public Boolean isOr() {
		return this.filterSetType.equals(FilterSetType.OR);
	}
	
	public Boolean isAnd() {
		return this.filterSetType.equals(FilterSetType.AND);
	}
	
	public List<Filter<Double>> getDoubleFilters() {
		return doubleFilterList;
	}
	
	public List<Filter<Integer>> getIntegerFilters() {
		return integerFilterList;
	}
	
	public List<Filter<String>> getStringFilters() {
		return stringFilterList;
	}
	
	public List<Filter<String>> getStringArrayFilters() {
		return stringArrayFilterList;
	}

	public Set<Label> getLabelSet() {
		return labelSet;
	}
}

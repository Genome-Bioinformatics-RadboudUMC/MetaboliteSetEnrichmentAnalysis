package org.umcn.tml.shared.datastructures.datatable.filters;

import org.springframework.stereotype.Component;

@Component
public class ApplyFiltersInteger extends ApplyFiltersAbstract<Integer> {

	@Override
	protected Boolean contains(Integer fieldValue, Filter<Integer> filter) {
		throw new RuntimeException(filter.getFilterType().toString() + " is not available for Integers.");	
	}

	@Override
	protected Boolean equals(Integer fieldValue, Filter<Integer> filter) {
		for (Integer filterValue : filter.getValueArray()) {
			if (fieldValue.equals(filterValue)) return true;			
		}

		return false;
	}
	
	@Override
	protected Boolean doesNotContain(Integer fieldValue, Filter<Integer> filter) {
		throw new RuntimeException(filter.getFilterType().toString() + " is not available for Integers.");	
	}
	
	@Override
	protected Boolean doesNotEqual(Integer fieldValue, Filter<Integer> filter) {
		for (Integer filterValue : filter.getValueArray()) {
			if (fieldValue.equals(filterValue)) return false;			
		}

		return true;		
	}

	@Override
	protected Boolean greaterThan(Integer fieldValue, Filter<Integer> filter) {
		for (int filterValue : filter.getValueArray()) {
			if (fieldValue > filterValue) return true;			
		}

		return false;
	}

	@Override
	protected Boolean greaterThanOrEqualTo(Integer fieldValue, Filter<Integer> filter) {
		for (int filterValue : filter.getValueArray()) {
			if (fieldValue.equals(filterValue)) return true;	
			if (fieldValue > filterValue) return true;			
		}

		return false;
	}

	@Override
	protected Boolean lessThan(Integer fieldValue, Filter<Integer> filter) {
		for (int filterValue : filter.getValueArray()) {
			if (fieldValue < filterValue) return true;			
		}

		return false;
	}

	@Override
	protected Boolean lessThanOrEqualTo(Integer fieldValue, Filter<Integer> filter) {
		for (int filterValue : filter.getValueArray()) {
			if (fieldValue.equals(filterValue)) return true;	
			if (fieldValue < filterValue) return true;			
		}

		return false;
	}

	@Override
	protected Boolean matches(Integer fieldValue, Filter<Integer> filter) {
		throw new RuntimeException(filter.getFilterType().toString() + " is not available for Integers.");	
	}
	
	

}

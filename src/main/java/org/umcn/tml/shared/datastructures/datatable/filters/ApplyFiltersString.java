package org.umcn.tml.shared.datastructures.datatable.filters;

import org.springframework.stereotype.Component;

@Component
public class ApplyFiltersString extends ApplyFiltersAbstract<String> {

	@Override
	protected Boolean contains(String fieldValue, Filter<String> filter) {
		for (String filterValue : filter.getValueArray()) {
			if (fieldValue.contains(filterValue)) return true;			
		}

		return false;
	}

	@Override
	protected Boolean equals(String fieldValue, Filter<String> filter) {
		for (String filterValue : filter.getValueArray()) {
			if (fieldValue.equals(filterValue)) return true;			
		}

		return false;
	}
	
	@Override
	protected Boolean doesNotContain(String fieldValue, Filter<String> filter) {
		for (String filterValue : filter.getValueArray()) {
			if (fieldValue.contains(filterValue)) return false;			
		}

		return true;
	}
	
	@Override
	protected Boolean doesNotEqual(String fieldValue, Filter<String> filter) {
		for (String filterValue : filter.getValueArray()) {
			if (fieldValue.equals(filterValue)) return false;			
		}

		return true;		
	}

	@Override
	protected Boolean greaterThan(String fieldValue, Filter<String> filter) {
		throw new RuntimeException(filter.getFilterType().toString() + " is not available for Strings.");
	}

	@Override
	protected Boolean greaterThanOrEqualTo(String fieldValue, Filter<String> filter) {
		throw new RuntimeException(filter.getFilterType().toString() + " is not available for Strings.");
	}

	@Override
	protected Boolean lessThan(String fieldValue, Filter<String> filter) {
		throw new RuntimeException(filter.getFilterType().toString() + " is not available for Strings.");
	}

	@Override
	protected Boolean lessThanOrEqualTo(String fieldValue, Filter<String> filter) {
		throw new RuntimeException(filter.getFilterType().toString() + " is not available for Strings.");
	}

	@Override
	protected Boolean matches(String fieldValue, Filter<String> filter) {
		for (String filterValue : filter.getValueArray()) {
			if (fieldValue.matches(filterValue)) return true;			
		}

		return false;
	}

}

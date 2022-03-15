package org.umcn.tml.shared.datastructures.datatable.filters;

import org.springframework.stereotype.Component;

@Component
public class ApplyFiltersDouble extends ApplyFiltersAbstract<Double> {
	

	@Override
	public final Boolean applyfilter(Double fieldValue, Filter<Double> filter) {		

		if (fieldValue == null) {
			return filter.isNullValue();
		}
		
		if (filter.isNanValue() != null && fieldValue == Double.NaN) {
			return filter.isNanValue();
		}
		
		if (filter.isNegativeInfinityValue() != null && fieldValue == Double.NEGATIVE_INFINITY) {
			return filter.isNegativeInfinityValue();
		}
		
		if (filter.isPositiveInfinityValue() != null && fieldValue == Double.POSITIVE_INFINITY) {
			return filter.isPositiveInfinityValue();
		}
		
		return super.applyfilter(fieldValue, filter);
	}

	@Override
	protected Boolean contains(Double fieldValue, Filter<Double> filter) {
		throw new RuntimeException(filter.getFilterType().toString() + " is not available for Doubles.");
	}
	
	@Override
	protected Boolean doesNotContain(Double fieldValue, Filter<Double> filter) {
		throw new RuntimeException(filter.getFilterType().toString() + " is not available for Doubles.");
	}

	@Override
	protected Boolean equals(Double fieldValue, Filter<Double> filter) {
		for (Double filterValue : filter.getValueArray()) {
			if (fieldValue.equals(filterValue)) return true;
		}

		return false;
	}
	
	@Override
	protected Boolean doesNotEqual(Double fieldValue, Filter<Double> filter) {
		for (Double filterValue : filter.getValueArray()) {
			if (fieldValue.equals(filterValue)) return false;
		}

		return true;		
	}

	@Override
	protected Boolean greaterThan(Double fieldValue, Filter<Double> filter) {
		for (Double filterValue : filter.getValueArray()) {
			if (fieldValue > filterValue) return true;
		}

		return false;
	}

	@Override
	protected Boolean greaterThanOrEqualTo(Double fieldValue, Filter<Double> filter) {
		for (Double filterValue : filter.getValueArray()) {
			if (fieldValue.equals(filterValue)) return true;	
			if (fieldValue > filterValue) return true;
		}

		return false;
	}

	@Override
	protected Boolean lessThan(Double fieldValue, Filter<Double> filter) {
		for (Double filterValue : filter.getValueArray()) {
			if (fieldValue < filterValue) return true;
		}

		return false;
	}

	@Override
	protected Boolean lessThanOrEqualTo(Double fieldValue, Filter<Double> filter) {
		for (Double filterValue : filter.getValueArray()) {
			if (fieldValue.equals(filterValue)) return true;
			if (fieldValue < filterValue) return true;
		}

		return false;
	}

	@Override
	protected Boolean matches(Double fieldValue, Filter<Double> filter) {
		throw new RuntimeException(filter.getFilterType().toString() + " is not available for Doubles.");
	}

}

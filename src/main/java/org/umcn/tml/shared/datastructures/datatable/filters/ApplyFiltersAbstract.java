package org.umcn.tml.shared.datastructures.datatable.filters;

public abstract class ApplyFiltersAbstract<T> {
	
	public Boolean applyfilter(T field, Filter<T> filter) {
		FilterType filterType = filter.getFilterType();
		
		if (field == null) {
			return filter.isNullValue();
		}
		
		switch(filterType) {
		case CONTAINS:
			return contains(field, filter);
		case EQUALS:
			return equals(field, filter);
		case DOES_NOT_CONTAIN:
			return doesNotContain(field, filter);
		case DOES_NOT_EQUAL:
			return doesNotEqual(field, filter);
		case GREATER_THAN:
			return greaterThan(field, filter);
		case GREATER_THAN_OR_EQUAL_TO:
			return greaterThanOrEqualTo(field, filter);
		case LESS_THAN:
			return lessThan(field, filter);
		case LESS_THAN_OR_EQUAL_TO:
			return lessThanOrEqualTo(field, filter);
		case MATCHES:
			return matches(field, filter);
		default:
			throw new RuntimeException(filter.getFilterType().toString() + " is not available for " + field.getClass().getName() +  ".");
		}
	}
	
	protected abstract Boolean contains(T field, Filter<T> filter);
	protected abstract Boolean equals(T field, Filter<T> filter);	
	protected abstract Boolean doesNotContain(T fieldValue, Filter<T> filter);
	protected abstract Boolean doesNotEqual(T field, Filter<T> filter);	
	protected abstract Boolean greaterThan(T field, Filter<T> filter);	
	protected abstract Boolean greaterThanOrEqualTo(T field, Filter<T> filter);	
	protected abstract Boolean lessThan(T field, Filter<T> filter);	
	protected abstract Boolean lessThanOrEqualTo(T field, Filter<T> filter);	
	protected abstract Boolean matches(T field, Filter<T> filter);

}

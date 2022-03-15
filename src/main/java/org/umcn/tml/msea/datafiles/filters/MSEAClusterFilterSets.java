package org.umcn.tml.msea.datafiles.filters;

import java.util.Set;

import org.umcn.tml.msea.datafiles.enums.MSEAStatisticalFilterLevel;
import org.umcn.tml.msea.datafiles.headers.labels.MSEAClusterLabel;
import org.umcn.tml.shared.datastructures.datatable.filters.Filter;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterSet;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterType;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterSet.FilterSetType;

public class MSEAClusterFilterSets {
	
	public static FilterSet includeClustersWithAberrantMetabolites(Set<String> metaboliteSet) {
		return includeClustersWithAberrantMetabolites(metaboliteSet.toArray(new String[metaboliteSet.size()]));
	}		
	
	public static FilterSet includeClustersWithAberrantMetabolites(String...metaboliteArray) {
		FilterSet filterSet = new FilterSet(FilterSetType.OR);
		filterSet.addStringFilter(new Filter<String>(MSEAClusterLabel.Aberrant_Metabolites, FilterType.CONTAINS, false, metaboliteArray));	
		return filterSet;
	}
	
	public static FilterSet includeEnrichedClusters(MSEAStatisticalFilterLevel sfl, double alpha) {
		FilterSet filterSet = new FilterSet(FilterSetType.OR);
		
		switch(sfl) {
		case BH:
			filterSet.addDoubleFilter(new Filter<Double>(MSEAClusterLabel.Hypergeometric_Test_P_Value_Bonferoni_Holm, FilterType.LESS_THAN, false, new Double[]{alpha}));	
			break;
		case FDR:
			filterSet.addDoubleFilter(new Filter<Double>(MSEAClusterLabel.Hypergeometric_Test_P_Value_Benjamini_Hochberg, FilterType.LESS_THAN, false, new Double[]{alpha}));	
			break;
		case No_Statistical_Filter:
			break;
		case Raw:
			filterSet.addDoubleFilter(new Filter<Double>(MSEAClusterLabel.Hypergeometric_Test_P_Value, FilterType.LESS_THAN, false, new Double[]{alpha}));	
			break;
		default:
			break;
		
		}
		
		return filterSet;
	}
}

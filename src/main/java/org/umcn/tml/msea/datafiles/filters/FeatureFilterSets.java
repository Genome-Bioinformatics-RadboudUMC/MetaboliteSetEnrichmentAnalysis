package org.umcn.tml.msea.datafiles.filters;

import java.util.Set;

import org.umcn.tml.shared.databases.enums.PathwayDatabase;
import org.umcn.tml.shared.datastructures.datatable.filters.Filter;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterSet;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterType;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterSet.FilterSetType;
import org.umcn.tml.msea.datafiles.headers.labels.FeatureTableLabel;

public class FeatureFilterSets {  	
	
	public static FilterSet includeFeatures(Set<String> featureIDSet) {
		return includeFeatures(featureIDSet.toArray(new String[featureIDSet.size()]));
	}
	    
    public static FilterSet includeFeatures(String...featureIDArray) {
    	FilterSet filterSet = new FilterSet(FilterSetType.AND);
    	filterSet.addStringFilter(new Filter<String>(FeatureTableLabel.Feature_ID, FilterType.EQUALS, false, featureIDArray));
    	
    	return filterSet;
    }
    
    public static FilterSet includeAberrantFeatures(double alpha) {
    	FilterSet filterSet = new FilterSet(FilterSetType.AND);  	    	
    	filterSet.addDoubleFilter(new Filter<Double>(FeatureTableLabel.P_Value, FilterType.LESS_THAN, false, new Double[]{alpha}));
    	
    	return filterSet;
    } 
    
    public static FilterSet excludeAberrantFeatures(double alpha) {
    	FilterSet filterSet = new FilterSet(FilterSetType.OR);  	
    	filterSet.addDoubleFilter(new Filter<Double>(FeatureTableLabel.P_Value, FilterType.GREATER_THAN_OR_EQUAL_TO, false, new Double[]{alpha}));
    	
    	return filterSet;
    }
    
    public static FilterSet includeFoldChanges(double foldchange, FilterType ft) {
    	FilterSet filterSet = new FilterSet(FilterSetType.OR);
    	Filter<Double> filter = new Filter<Double>(FeatureTableLabel.Fold_Change, ft, false, new Double[]{foldchange});
    	filter.setNanValue(false);
		filterSet.addDoubleFilter(filter); 
    	
    	return filterSet;
    } 
    
    public static FilterSet includeAnnotatedFeatures() {
    	FilterSet filterSet = new FilterSet(FilterSetType.OR);
    	filterSet.addStringFilter(new Filter<String>(FeatureTableLabel.HMP, FilterType.DOES_NOT_EQUAL, false, new String[]{""}));
    	filterSet.addStringFilter(new Filter<String>(FeatureTableLabel.KEGG_Entry, FilterType.DOES_NOT_EQUAL, false, new String[]{""}));
    	
    	return filterSet;
    }
    
    public static FilterSet includeMetabolites(Set<String> metaboliteSet) {
    	return includeMetabolites(metaboliteSet.toArray(new String[metaboliteSet.size()]));
    }
        
    public static FilterSet includeMetabolites(String...metaboliteArray) {
    	FilterSet filterSet = new FilterSet(FilterSetType.OR);
    	filterSet.addStringFilter(new Filter<String>(FeatureTableLabel.HMP, FilterType.EQUALS, false, metaboliteArray));
    	filterSet.addStringFilter(new Filter<String>(FeatureTableLabel.KEGG_Entry, FilterType.EQUALS, false, metaboliteArray));
    	
    	return filterSet;
    }
    
    public static FilterSet includeMSEAFeatures(PathwayDatabase pd) {
    	FilterSet filterSet = new FilterSet(FilterSetType.OR); 	
    	
    	switch(pd) {
		case ALL:
			filterSet.addStringFilter(new Filter<String>(FeatureTableLabel.MSEA_Metabolite_Set_ID, FilterType.DOES_NOT_EQUAL, false, new String[]{"", "null"}));
			break;
		case KEGG:
		case SMPDB:
		default:
			filterSet.addStringFilter(new Filter<String>(FeatureTableLabel.MSEA_Metabolite_Set_ID, FilterType.MATCHES, false, new String[]{".*"+pd.getIDFormat()+".*"}));
			break;
    	}    	
    	
    	return filterSet;
    }
	
    public static FilterSet includeMSEAFeatures(String...pathwayIDArray) {
		FilterSet filterSet = new FilterSet(FilterSetType.AND);		
		filterSet.addStringFilter(new Filter<String>(FeatureTableLabel.MSEA_Metabolite_Set_ID, FilterType.CONTAINS, false, pathwayIDArray));    
		
		return filterSet;
	}
}

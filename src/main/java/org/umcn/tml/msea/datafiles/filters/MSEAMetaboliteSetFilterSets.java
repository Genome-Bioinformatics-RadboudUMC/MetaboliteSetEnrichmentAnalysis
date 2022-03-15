package org.umcn.tml.msea.datafiles.filters;

import java.util.Set;

import org.umcn.tml.shared.databases.enums.KEGGPathwayCategories;
import org.umcn.tml.shared.databases.enums.PathwayDatabase;
import org.umcn.tml.shared.databases.enums.SMPDBPathwayCategories;
import org.umcn.tml.shared.datastructures.datatable.filters.Filter;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterSet;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterType;
import org.umcn.tml.shared.datastructures.datatable.filters.FilterSet.FilterSetType;
import org.umcn.tml.msea.datafiles.enums.MSEAStatisticalFilterLevel;
import org.umcn.tml.msea.datafiles.headers.labels.MSEAMetaboliteSetLabel;

public class MSEAMetaboliteSetFilterSets {	
	
	public static FilterSet includeCluster(String clusterID) {
		FilterSet filterSet = new FilterSet(FilterSetType.AND);
		filterSet.addStringFilter(new Filter<String>(MSEAMetaboliteSetLabel.Cluster_ID, FilterType.EQUALS, false, new String[]{clusterID}));
		
		return filterSet;
	}
	
	public static FilterSet includeEnrichedMetaboliteSets(MSEAStatisticalFilterLevel statisticalFilterLevel, double alpha) {
		FilterSet filterSet = new FilterSet(FilterSetType.AND);		
		if (!statisticalFilterLevel.equals(MSEAStatisticalFilterLevel.No_Statistical_Filter)) {
			filterSet.addDoubleFilter(new Filter<Double>(statisticalFilterLevel.getMSEAMetaboliteSetLabel(), FilterType.LESS_THAN, false, new Double[]{alpha}));    
		}
		
		return filterSet;
	}
	
	public static FilterSet excludeEnrichedMetaboliteSets(MSEAStatisticalFilterLevel statisticalFilterLevel, double alpha) {
		FilterSet filterSet = new FilterSet(FilterSetType.AND);		
		if (!statisticalFilterLevel.equals(MSEAStatisticalFilterLevel.No_Statistical_Filter)) {
			filterSet.addDoubleFilter(new Filter<Double>(statisticalFilterLevel.getMSEAMetaboliteSetLabel(), FilterType.GREATER_THAN_OR_EQUAL_TO, false, new Double[]{alpha}));    
		}
		
		return filterSet;
	}
	
	public static FilterSet includeStatisticallyUnenrichedMetaboliteSets(MSEAStatisticalFilterLevel statisticalFilterLevel, double alpha) {
		FilterSet filterSet = new FilterSet(FilterSetType.AND);		
		if (!statisticalFilterLevel.equals(MSEAStatisticalFilterLevel.No_Statistical_Filter)) {
			filterSet.addDoubleFilter(new Filter<Double>(statisticalFilterLevel.getMSEAMetaboliteSetLabel(), FilterType.GREATER_THAN_OR_EQUAL_TO, false, new Double[]{alpha}));    
		}
		
		return filterSet;
	}
	
	public static FilterSet includeMetaboliteSets(String...metaboliteSetArray) {
		FilterSet filterSet = new FilterSet(FilterSetType.OR);
		filterSet.addStringFilter(new Filter<String>(MSEAMetaboliteSetLabel.Metabolite_Set_ID, FilterType.EQUALS, false, metaboliteSetArray));	
		return filterSet;
	}
	
	public static FilterSet includeMetaboliteSetsWithAssociatedMetabolites(Set<String> metaboliteSet) {
		return includeMetaboliteSetsWithAssociatedMetabolites(metaboliteSet.toArray(new String[metaboliteSet.size()]));
	}
	
	public static FilterSet includeMetaboliteSetsWithAssociatedMetabolites(String...metaboliteArray) {
		FilterSet filterSet = new FilterSet(FilterSetType.OR);
		filterSet.addStringFilter(new Filter<String>(MSEAMetaboliteSetLabel.Metabolite_Set_Metabolites, FilterType.CONTAINS, false, metaboliteArray));	
		return filterSet;
	}
	
	public static FilterSet includeMetaboliteSetWithAberrantMetabolites(Set<String> metaboliteSet) {
		return includeMetaboliteSetWithAberrantMetabolites(metaboliteSet.toArray(new String[metaboliteSet.size()]));
	}	

	public static FilterSet includeMetaboliteSetWithAberrantMetabolites(String...metaboliteArray) {
		FilterSet filterSet = new FilterSet(FilterSetType.OR);
		filterSet.addStringFilter(new Filter<String>(MSEAMetaboliteSetLabel.Aberrant_Metabolites, FilterType.CONTAINS, false, metaboliteArray));	
		return filterSet;
	}
	
	public static FilterSet includePathwayDatabase(PathwayDatabase pd) {
		FilterSet filterSet = new FilterSet(FilterSetType.OR);
		switch(pd) {
		case KEGG:
			filterSet.addStringFilter(new Filter<String>(MSEAMetaboliteSetLabel.Metabolite_Set_ID, FilterType.CONTAINS, false, new String[]{"hsa"}));
			break;
		case SMPDB:
			filterSet.addStringFilter(new Filter<String>(MSEAMetaboliteSetLabel.Metabolite_Set_ID, FilterType.CONTAINS, false, new String[]{"SMP"}));
			break;
		default:
			throw new RuntimeException("Code for processing this database still has to be written!");		
		}
		return filterSet;
	}
	
	public static FilterSet includeMetabolicPathways() {
		FilterSet filterSet = new FilterSet(FilterSetType.OR);
		
		filterSet.addStringFilter(new Filter<String>(MSEAMetaboliteSetLabel.Metabolite_Set_Category, FilterType.EQUALS, false, new String[]{SMPDBPathwayCategories.Metabolic.name()}));
		filterSet.addStringFilter(new Filter<String>(MSEAMetaboliteSetLabel.Metabolite_Set_Category, FilterType.EQUALS, false, KEGGPathwayCategories.Metabolism.getSubCategories()));
		
		return filterSet;
	}
	
	public static FilterSet includeMetaboliteSetsWithSpecificNumberOfAberrantFeatures(int aberrantFeatures) {
		FilterSet filterSet = new FilterSet(FilterSetType.OR);
		filterSet.addIntegerFilter(new Filter<Integer>(MSEAMetaboliteSetLabel.Hypergeometric_Test_n11, FilterType.EQUALS, false, new Integer[]{aberrantFeatures}));
		
		return filterSet;
	}
}

package org.umcn.tml.msea.clustering;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.umcn.tml.msea.clustering.MSEAClusteringApp.ClusterStatistics;
import org.umcn.tml.msea.datafiles.headers.labels.MSEAMetaboliteSetLabel;
import org.umcn.tml.shared.databases.enums.KEGGPathwayCategories;
import org.umcn.tml.shared.databases.enums.SMPDBPathwayCategories;
import org.umcn.tml.shared.datastructures.SortMapOnValues;
import org.umcn.tml.shared.datastructures.SortMapOnValues.Order;
import org.umcn.tml.shared.datastructures.datatable.DataTableRow;
import org.umcn.tml.shared.statistics.FisherExactTest;
import org.umcn.tml.shared.textmining.PorterStemmer;
import org.umcn.tml.shared.textmining.Stopwords;

public class MetaboliteSetCluster {
	private final String id;
	private final Set<String> aberrantFeatures = new LinkedHashSet<>();
	private final Set<String> aberrantMetabolites = new LinkedHashSet<>();
	private final Set<String> normalFeatures = new LinkedHashSet<>();
	private final Set<String> normalMetabolites = new LinkedHashSet<>();
	private final Map<String, String> metaboliteSets = new LinkedHashMap<>();
	private final Map<String, String> metaboliteSetCategories = new LinkedHashMap<>();
	private final Set<String> similarClusters = new LinkedHashSet<>();
	private final Map<String, Integer> keyWords = new LinkedHashMap<>();

	private Double highestFCIncrease;
	private Double highestFCdecrease;
	private int fcNans;
	
	private DataTableRow representativeMetaboliteSet;

	public MetaboliteSetCluster(int id) {
		this.id = "Cluster_" + id;
	}
	
	private boolean isMetabolic(String metaboliteSetCategory) {		
		List<String> keggMetabolicCategories =  Arrays.asList(KEGGPathwayCategories.Metabolism.getSubCategories());
		if (keggMetabolicCategories.contains(metaboliteSetCategory)) {
			return true;
		} else if (metaboliteSetCategory.equals(SMPDBPathwayCategories.Metabolic.name())) {
			return true;
		} else {
			return false;
		}
	}

	public void setRepresentativeMetaboliteSet(DataTableRow metaboliteSetRow) {
		if (representativeMetaboliteSet == null) {
			representativeMetaboliteSet = metaboliteSetRow;
		} else {			
			double newPvalue = metaboliteSetRow.getDouble(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value);
			int newN11 = metaboliteSetRow.getInteger(MSEAMetaboliteSetLabel.Hypergeometric_Test_n11);
			boolean newMetabolic = isMetabolic(metaboliteSetRow.getString(MSEAMetaboliteSetLabel.Metabolite_Set_Category));
			double currentPvalue = this.representativeMetaboliteSet.getDouble(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value);
			int currentN11 = this.representativeMetaboliteSet.getInteger(MSEAMetaboliteSetLabel.Hypergeometric_Test_n11);
			boolean currentMetabolic = isMetabolic(representativeMetaboliteSet.getString(MSEAMetaboliteSetLabel.Metabolite_Set_Category));
			
			if (newN11 > currentN11) {
				representativeMetaboliteSet = metaboliteSetRow;
			} else if (newN11 < currentN11) {
				return;
			}  else if (newPvalue < currentPvalue) {
				representativeMetaboliteSet = metaboliteSetRow;
			}else if (newPvalue > currentPvalue) { 
				return;
			} else if ((!currentMetabolic) && newMetabolic) {
				representativeMetaboliteSet = metaboliteSetRow;
			} else {
				return;
			}			
		}				
	}

	public void setAberrantFeatures(String[] aberrantFeatures) {
		this.aberrantFeatures.addAll(Arrays.asList(aberrantFeatures));
	}

	public void setAberrantMetabolites(String[] aberrantMetabolites) {
		this.aberrantMetabolites.addAll(Arrays.asList(aberrantMetabolites));
	}

	public void setNormalFeatures(String[] normalFeatures) {
		this.normalFeatures.addAll(Arrays.asList(normalFeatures));
	}

	public void setNormalMetabolites(String[] normalMetabolites) {
		this.normalMetabolites.addAll(Arrays.asList(normalMetabolites));
	}

	public void setMetaboliteSets(String metaboliteSetID, String metaboliteSetName) {
		this.metaboliteSets.put(metaboliteSetID, metaboliteSetName);
	}

	public void setMetaboliteSetCategories(String metaboliteSetID, String metaboliteSetCategory) {
		this.metaboliteSetCategories.put(metaboliteSetID, metaboliteSetCategory);
	}

	public void setSimilarClusters(String clusterID) {
		this.similarClusters.add(clusterID);
	}	

	public void setHighestFCIncrease(Double highestFCIncrease) {
		if (highestFCIncrease == null) return;
		if (this.highestFCIncrease == null ||
				highestFCIncrease > this.highestFCIncrease) {

			this.highestFCIncrease = highestFCIncrease;
		}
	}

	public void setHighestFCdecrease(Double highestFCdecrease) {
		if (highestFCdecrease == null) return;
		if (this.highestFCdecrease == null ||
				highestFCdecrease > this.highestFCdecrease) {
			this.highestFCdecrease = highestFCdecrease;
		}
	}

	public void setFcNans(int fcNans) {
		this.fcNans = fcNans;
	}

	public void setKeyWords(String metaboliteSetName) {
		//PC	Phosphatidylcholines
		//PE	phosphatidylethanolamine
		//CL	cardiolipin 
		//TG	Triacylglycerol

		String[] wordArray = metaboliteSetName.toLowerCase().split(" ");//.replace("/", " ")

		Set<String> wordSet = new LinkedHashSet<String>();

		for (String word : wordArray) {
			word = word.replace(".", "").replace("(", "").replace(")", "").replace(",", "").replace("[", "").replace("]", "").replace("\"", "").replaceAll("^-", "");//.replace(":", "").replaceAll("[0-9]", "")
			if (word.equals("")) continue;
			if (!word.matches(".*[a-z]+.*")) continue;
			if (word.length() <= 1) continue;
			wordSet.add(word);
		}

		for (String word : wordSet) {				
			//remove stop words
			if (Stopwords.isStopword(word)) continue;
			if (word.equals("#n/a")) continue;
			if (word.equals("pathway")) continue;
			if (word.equals("due")) continue;
			if (word.equals("type")) continue;
			if (word.equals("action")) continue;
			if (word.equals("metabolism")) continue;
			if (word.equals("biosynthesis")) continue;

			//remove stemmed stop words
			PorterStemmer porterStemmer = new PorterStemmer();
			porterStemmer.add(word.toCharArray(), word.length());
			porterStemmer.stem();
			String subjectStemmedWord = porterStemmer.toString();
			if (Stopwords.isStemmedStopword(subjectStemmedWord)) continue;

			int count;
			if (keyWords.containsKey(word)) {
				count = keyWords.get(word) + 1;
			} else {
				count = 1;
			}

			keyWords.put(word, count);

		}		
	}

	//Getters//////////////////////////////////////////////////////////////////////////////////////////
	public String getID() {
		return id;
	}	

	public String getAberrantFeatures(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return String.join(";", aberrantFeatures);
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getString(MSEAMetaboliteSetLabel.Aberrant_Features);		
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());
		}
	}
	
	public Set<String> getAberrantFeatureSet(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return aberrantFeatures;
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			Set<String> aberrantFeatures = new LinkedHashSet<>();
			Collections.addAll(aberrantFeatures, representativeMetaboliteSet.getString(MSEAMetaboliteSetLabel.Aberrant_Features).split(";"));
			return aberrantFeatures;
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());
		}
	}

	public Set<String> getAberrantMetaboliteSet(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return aberrantMetabolites;
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			Set<String> aberrantMetabolites = new LinkedHashSet<>();
			Collections.addAll(aberrantMetabolites, representativeMetaboliteSet.getString(MSEAMetaboliteSetLabel.Aberrant_Metabolites).split(";"));
			return aberrantMetabolites;
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());
		}
	}

	public String getAberrantMetabolites(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return String.join(";", aberrantMetabolites);
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getString(MSEAMetaboliteSetLabel.Aberrant_Metabolites);		
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());
		}
	}

	public String getNormalFeatures(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return String.join(";", normalFeatures);
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getString(MSEAMetaboliteSetLabel.Normal_Features);		
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());
		}
	}

	public int getN11(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return aberrantFeatures.size();
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getInteger(MSEAMetaboliteSetLabel.Hypergeometric_Test_n11);
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());		
		}
	}

	public int getN12(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
		 return getN11(ClusterStatistics.Representative_Pathway_Based)+getN21(ClusterStatistics.Representative_Pathway_Based)-aberrantFeatures.size();  
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getInteger(MSEAMetaboliteSetLabel.Hypergeometric_Test_n12);
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());		
		}
	}

	public int getN21(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return normalFeatures.size();
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getInteger(MSEAMetaboliteSetLabel.Hypergeometric_Test_n21);
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());		
		}
	}

	public int getN22(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			 return getN12(ClusterStatistics.Representative_Pathway_Based)+getN22(ClusterStatistics.Representative_Pathway_Based)-normalFeatures.size();  
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getInteger(MSEAMetaboliteSetLabel.Hypergeometric_Test_n22);
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());		
		}
	}

	public String getNormalMetabolites(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return String.join(";", normalMetabolites);
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getString(MSEAMetaboliteSetLabel.Normal_Metabolites);		
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());
		}
	}

	public String getMetaboliteSetIDs() {
		return String.join(";", metaboliteSets.keySet());
	}

	public String getMetaboliteSetNames() {
		return String.join(";", metaboliteSets.values());
	}

	public String getMetaboliteSetCategories() {
		return String.join(";", metaboliteSetCategories.values());
	}
	
	public DataTableRow getRepresentativeMetaboliteSet() {
		return this.representativeMetaboliteSet;
	}
	
	public String getRepresentativeMetaboliteSetID() {
		return representativeMetaboliteSet.getString(MSEAMetaboliteSetLabel.Metabolite_Set_ID);
	}
	
	public String getRepresentativeMetaboliteSetName() {
		return representativeMetaboliteSet.getString(MSEAMetaboliteSetLabel.Metabolite_Set_Name);
	}
	
	public String getRepresentativeMetaboliteSetCategory() {
		return representativeMetaboliteSet.getString(MSEAMetaboliteSetLabel.Metabolite_Set_Category);
	}
	
	public String getSimilarClusters() {
		return String.join(";", similarClusters);
	}

	public Double getHighestFCIncrease(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return highestFCIncrease;
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getDouble(MSEAMetaboliteSetLabel.Highest_Feature_FC_Increase);		
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());
		}
	}

	public Double getHighestFCdecrease(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return highestFCdecrease;
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getDouble(MSEAMetaboliteSetLabel.Highest_Feature_FC_Decrease);		
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());
		}
	}

	public int getFcNans(ClusterStatistics clusterStatistics) {
		switch(clusterStatistics) {
		case Cluster_Based:
			return fcNans;
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getInteger(MSEAMetaboliteSetLabel.Feature_FC_NaNs);		
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());
		}
	}

	public double getHgtPvalue(ClusterStatistics clusterStatistics, FisherExactTest fisherExactTest) {
		switch(clusterStatistics) {
		case Cluster_Based:								
			return fisherExactTest.calculateHypergeomP(getN11(clusterStatistics), getN12(clusterStatistics), getN21(clusterStatistics), getN22(clusterStatistics));
		case Original_MSEA_P_VALUE:
		case Representative_Pathway_Based:
			return representativeMetaboliteSet.getDouble(MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value);	
		default:
			throw new RuntimeException("This enum value has not been implemented: " + clusterStatistics.toString());			
		}
	}

	public String getKeyWords() {
		Map<String, Integer> pathwayNameWordMap = SortMapOnValues.sortMapByValues(keyWords, Order.DESCENDING);

		String clusterKeywords = "";
		int i = 0;
		for (String pathwayNameWord : pathwayNameWordMap.keySet()) {
			if (i == 0) {
				clusterKeywords = pathwayNameWord;
			} else if (i < 5) {
				clusterKeywords = clusterKeywords + ";" + pathwayNameWord;
			} else {
				break;
			}
			i++;
		}

		return clusterKeywords;
	}
}

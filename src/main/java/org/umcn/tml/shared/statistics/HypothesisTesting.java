package org.umcn.tml.shared.statistics;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.TDistribution;
import org.springframework.stereotype.Component;
import org.umcn.tml.shared.datastructures.SortMapOnValues;
import org.umcn.tml.shared.datastructures.SortMapOnValues.Order;
import org.umcn.tml.shared.util.BasicMath;

@Component
public class HypothesisTesting {
	private enum BHCorrectionType {
		BONFERONI_HOLM, BENJAMINI_HOCHBERG;
	}
	
	public Double getZScore (List<Double> meanControlIntensities, Double meanPatient) {
		double meanControls = BasicMath.mean(meanControlIntensities);
		double standardDev = BasicMath.standardDeviation(false, meanControlIntensities);

		return (meanPatient-meanControls)/standardDev;
	}

	public Double getTValue (List<Double> meanControlIntensities, Double meanPatient) {
		double meanControls = BasicMath.mean(meanControlIntensities);
		double standardDev = BasicMath.standardDeviation(false, meanControlIntensities);
		int n = meanControlIntensities.size();
		
		return (meanPatient-meanControls)/(standardDev-Math.sqrt(n));
	}

	public Double getPValueTDistribution (double meanControls, double meanPatient, List<Double> values, Double z) {
		//if (meanControls > 0 && meanPatient == 0) {
		//	return 0.0;	
	    //} else 
		if (meanControls == 0 && meanPatient > 0) {
	    	return 0.0;	
		} else
		if (meanControls == 0 && meanPatient == 0) {
			return 1.0;	
	    } else {
			int n = values.size();
			int df = n-1;
			z = Math.abs(z);
			
			double pvalue = 2.0 * (1.0 - new TDistribution(df).cumulativeProbability(z));
			
			if (pvalue>1.0) pvalue = 1.0;
			
			return pvalue;
		}
	}

	public Map<String, Double> bonferroniHolmCorrection (Map<String, Double> pValueMap) {
		return bhCorrection(pValueMap, null, BHCorrectionType.BONFERONI_HOLM);
	}

	public Map<String, Double> bonferroniHolmCorrection(Map<String, Double> pValueMap, Double alpha) {
		return bhCorrection(pValueMap, alpha, BHCorrectionType.BONFERONI_HOLM);
	}
	
	public Map<String, Double> benjaminiHochbergCorrection(Map<String, Double> pValueMap) {
		return bhCorrection(pValueMap, null, BHCorrectionType.BENJAMINI_HOCHBERG);
		
	}
	
	public Map<String, Double> benjaminiHochbergCorrection(Map<String, Double> pValueMap, Double alpha) {
		return bhCorrection(pValueMap, alpha, BHCorrectionType.BENJAMINI_HOCHBERG);
	}
	
	private Map<String, Double> bhCorrection(Map<String, Double> pValueMap, Double alpha, BHCorrectionType bhCorrectiontype) {
		Map<String, Double> sortedpValueMap = SortMapOnValues.sortMapByValues(pValueMap, Order.ASCENDING);
		
		int m = sortedpValueMap.values().size();

		Map<String, Double> adjustedPValueMap = new LinkedHashMap<String, Double>();
		int i = 1;
		for (String id : sortedpValueMap.keySet()) {
			Double pValue = sortedpValueMap.get(id);
			
			if (alpha == null) {
				//no threshold check
			} else if (bhCorrectiontype.equals(BHCorrectionType.BONFERONI_HOLM)) {
				if (pValue >= (alpha/(m-i+1))) break;
			} else {
				if (pValue >= (i*alpha/m)) break;
			}			
			
			Double adjustedPValue;
			if (bhCorrectiontype.equals(BHCorrectionType.BONFERONI_HOLM)) {
				adjustedPValue = pValue*(m-i+1);
			} else {
				adjustedPValue = pValue*m/i;
			}

			if (adjustedPValue > 1.0) adjustedPValue = 1.0;

			adjustedPValueMap.put(id, adjustedPValue);

			i++;
		}

		return adjustedPValueMap;
	}
}

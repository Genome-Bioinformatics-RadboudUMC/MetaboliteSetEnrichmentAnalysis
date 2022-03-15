package org.umcn.tml.shared.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

public class BasicMath {
	
	public static <N extends Number> double percentage(N part, N total) {
		return (part.doubleValue()/total.doubleValue())*100;
	}

	public static <N extends Number> List<Double> percentage (List<N> partList, List<N> totalList) {
		List<Double> percentageList = new ArrayList<>();
		for (int i = 0; i < partList.size(); i++) {
			double part = partList.get(i).doubleValue();
			double total = totalList.get(i).doubleValue();
			try {
				percentageList.add(percentage(part, total));
			} catch (ArithmeticException ae) {
				percentageList.add(0.0);
			}			
		}
		return percentageList;
	}	
	
	public static <N extends Number> double mean (N[] numberArray) {
		return mean(Arrays.asList(numberArray));
	}
	
	public static <N extends Number> double mean (List<N> numberList) {
		SummaryStatistics summaryStatistics = new SummaryStatistics();
		for (N number : numberList) {
			summaryStatistics.addValue(number.doubleValue());
		}
		return summaryStatistics.getMean();
	}
	
	public static <N extends Number> double median (N[] numberArray) {
		return percentile(50, numberArray);
	}
	
	public static <N extends Number> double median (List<N> numberList) {
		return percentile(50, numberList); 
	}
	
	public static <N extends Number> double min (N[] numberArray) {
		return min(Arrays.asList(numberArray));
	}
	
	public static <N extends Number> double min (List<N> numberList) {
		DescriptiveStatistics descriptiveStatistics = getDescriptiveStatitics(numberList);
		return descriptiveStatistics.getMin();
	}
	
	public static <N extends Number> double max (N[] numberArray) {
		return max(Arrays.asList(numberArray));
	}
	
	public static <N extends Number> double max (List<N> numberList) {
		DescriptiveStatistics descriptiveStatistics = getDescriptiveStatitics(numberList);
		return descriptiveStatistics.getMax();
	}
	
	public static <N extends Number> double percentile (double percentile, N[] numberArray) {
		return percentile(percentile, Arrays.asList(numberArray));
	}
		
	public static <N extends Number> double percentile (double percentile, List<N> numberList) {
		DescriptiveStatistics descriptiveStatistics = getDescriptiveStatitics(numberList);
		return descriptiveStatistics.getPercentile(percentile);
	}
	
	private static <N extends Number> DescriptiveStatistics getDescriptiveStatitics(List<N> numberList) {
		DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
		for (N number : numberList) {
			descriptiveStatistics.addValue(number.doubleValue());
		}
		return descriptiveStatistics;
	}
	
	public static <N extends Number> double standardErrorOfTheMean (boolean populationStatistics, List<N> numberList) {
		return standardErrorOfTheMean(populationStatistics, numberList.toArray(new Number[numberList.size()]));
	}
	
	public static <N extends Number> double standardErrorOfTheMean (boolean populationStatistics, N[] numberArray) {
		int n = numberArray.length;
		double sd = standardDeviation(populationStatistics, numberArray);
		return sd/sqrt(n);
	}
	
	public static <N extends Number> double standardDeviation (boolean populationStatistics, List<N> numberList) {
		return standardDeviation(populationStatistics, numberList.toArray(new Number[numberList.size()]));
	}
	
	public static <N extends Number> double standardDeviation (boolean populationStatistics, N[] numberArray) {
		return sqrt(variance(populationStatistics, numberArray));
	}
	
	public static <N extends Number> double variance (boolean populationStatistics, List<N> numberList) {
		return variance(populationStatistics, numberList.toArray(new Number[numberList.size()]));
	}
	
	public static <N extends Number> double variance (boolean populationStatistics, N[] numberArray) {
		int n = numberArray.length;
		if (!populationStatistics){
			n-=1;
		}
		double mean = mean(numberArray);
		double sum = 0;
		for(N number : numberArray) {
			sum += pow(number.doubleValue()-mean, 2);
		}
		return sum/(n);
	}
	
	public static List<Double> divideNumbersByAConstant (double constant, List<Double> numberList) {
		List<Double> newList = new ArrayList<>();
		for (double number : numberList) {
			newList.add(number/constant);
		}
		return newList;
	}
	
	public static Double log (Number value, int log) {
		if (value.doubleValue() == 0) {
			return 0.0;
		} else {
			return Math.log10(value.doubleValue()) / Math.log10(log);
		}
	}
	
	public static <N extends Number> List<Double> log (List<N> valueList, int log) {
		List<Double> logValueList = new ArrayList<>();
		for (N value : valueList) {
			logValueList.add(log(value, 2));
		}
		return logValueList;
	}
	
	public static String roundOffToDecPlaces(double val, int decimals) {
	    return String.format("%."+decimals+"f", val);
	}
	
	//Assumes the value is NOT included in the list! So considers N to be valueList.size() + 1.
	public static <N extends Number> Integer rankOfValueComparedToList(N x, List<N> valueList, boolean ascending) {
		Integer valuesThatRankHigherThanX = 0;
		Integer valuesThatTieWithX = 0;
		for (int i = 0; i < valueList.size(); i++) {
			if (ascending && x.doubleValue() > valueList.get(i).doubleValue()) {
				valuesThatRankHigherThanX += 1;
			} else if (!ascending && x.doubleValue() < valueList.get(i).doubleValue()) {
				valuesThatRankHigherThanX += 1;
			} else if (x.doubleValue() == valueList.get(i).doubleValue()) {
				valuesThatTieWithX += 1;		
			}
		}
		
		//we add 0.5 to ensure everything is "rounded up". intValue() will simply remove any decimals, so it will persistently "round down". 
		//Example tie = 3
			//without 0.5:  tieCorrection = 3/2 = 1.5 -> intValue() = 1		(But we want it to be rounded up as 2)
			//with 0.5: tieCorrection = (3/2)+0.5 = 2 -> intValue() = 2
		//Example tie = 4
			//without 0.5: tieCorrection = 4/2 = 2 -> intValue() = 2
			//with 0.5: tieCorrection = (4/2)+0.5 = 2.5 -> intValue() = 2	(Even numbered ties are unaffected by the mechanism, so this remains 2, as we want it to be.)
		Double tieCorrection = (valuesThatTieWithX.doubleValue() / 2) + 0.5;	
		int rank = valuesThatRankHigherThanX + tieCorrection.intValue() + 1; 
		return rank;
	}
}

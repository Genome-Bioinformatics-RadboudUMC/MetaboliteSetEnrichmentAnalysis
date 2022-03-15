package org.umcn.tml.shared.statistics;

//This class is modified version of https://github.com/ctlab/genequery-java/blob/master/commons/src/main/java/com/genequery/commons/math/FisherExactTest.java
public class FisherExactTest {

  /**
   * Pre-calculated log factorial value.
   */
  private double[] logFactorial;

  public FisherExactTest (int n) {
	  logFactorial = new double[n + 1];
	  
	  logFactorial[0] = 0.0;
	  for (int i = 1; i <= n; i++) {
		  logFactorial[i] = logFactorial[i - 1] + Math.log(i);
	  }	  
  }

  /**
   * Calculate a right p-value for ru.ifmo.gq.console.fisher.Fisher's Exact Test.
   */
  public double rightTail(int a, int b, int c, int d) {
    double p_sum = 0.0d;
    double p = calculateHypergeomP(a, b, c, d);
    while (c >= 0 && b >= 0) {
      p_sum += p;
      if (b == 0 || c == 0) break;
      ++a;
      --b;
      --c;
      ++d;
      p = calculateHypergeomP(a, b, c, d);
    }
    return p_sum;
  }

  public double calculateHypergeomP(int a, int b, int c, int d) {
    return Math.exp(logFactorial[a + b] +
        logFactorial[c + d] +
        logFactorial[a + c] +
        logFactorial[b + d] -
        logFactorial[a + b + c + d] -
        logFactorial[a] -
        logFactorial[b] -
        logFactorial[c] -
        logFactorial[d]);
  }
}
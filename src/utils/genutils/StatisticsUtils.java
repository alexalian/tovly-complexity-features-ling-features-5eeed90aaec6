/**
 * 
 */
package src.utils.genutils;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

/**
 * @author svajjala A utilities class that uses Apache Commons Math package to
 *         calculate a variety of statistical functions.
 */
public class StatisticsUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/**
	 * Calculates Spearman's correlation between two number arrays.
	 * 
	 * @param al1 - ArrayList object with first group of numbers
	 * @param al2 - ArrayList object with second group of numbers
	 * @return a value between -1 and 1
	 * @throws Exception
	 */
	public static double calcSpearmans(ArrayList<Double> al1, ArrayList<Double> al2) throws Exception {
		double spearman = 0.0;
		SpearmansCorrelation sc = new SpearmansCorrelation();
		double[] dummy1 = new double[al1.size()];
		double[] dummy2 = new double[al2.size()];
		for (int i = 0; i < al1.size(); i++) {
			dummy1[i] = al1.get(i);
		}

		for (int i = 0; i < al2.size(); i++) {
			dummy2[i] = al2.get(i);
		}
		spearman = sc.correlation(dummy1, dummy2);
		return spearman;
	}

	/**
	 * Calculates different prediction errors between actual and predicted values
	 * from a regression model.
	 * 
	 * @param al1 - ArrayList object containing actual values (double)
	 * @param al2 - ArrayList object containing predicted values.
	 * @return A HashMap object containing Mean absolute Error, RMSE, Exact Match
	 *         Accuracy and Adjacent Accuracy. The last two are percentages.
	 */
	public static HashMap<String, Double> calcPredictionPerformance(ArrayList<Double> al1, ArrayList<Double> al2) {
		double[] dummy1 = new double[al1.size()];
		double[] dummy2 = new double[al2.size()];
		HashMap<String, Double> result = new HashMap<String, Double>();
		int n = dummy1.length;
		double diff = 0.0;
		double within1 = 0.0;
		double mae = 0.0;
		double exactmatches = 0.0;
		// D1: Actual scores, D2: Scores from some Evaluator:

		for (int i = 0; i < dummy1.length; i++) {
			if (dummy1[i] == dummy2[i]) {
				exactmatches++;
			}
			diff += (dummy1[i] - dummy2[i]) * (dummy1[i] - dummy2[i]);
			mae += Math.abs(dummy1[i] - dummy2[i]);
			if (Math.abs(dummy1[i] - dummy2[i]) < 2) {
				within1++;
			}
		}
		result.put("MeanAbsoluteError", mae / n);
		result.put("RMSE", Math.sqrt(diff / n));
		result.put("ExactAccuracy", exactmatches / n);
		result.put("AdjacentAccuracy", within1 / n);
		return result;
	}

	/**
	 * Performs wilcoxon's signed rank test to check if the rankings in two lists of
	 * numbers are significantly different from each other.
	 * 
	 * @param al1 List 1
	 * @param al2 List 2
	 * @returns a HashMap with two elements - pvalue and wilcoxon's signed rank test
	 *          statistic.
	 * @throws Exception
	 */
	public static HashMap<String, Double> performWilcoxonsTest(ArrayList<Double> al1, ArrayList<Double> al2)
			throws Exception {
		double[] dummy1 = new double[al1.size()];
		double[] dummy2 = new double[al2.size()];
		HashMap<String, Double> result = new HashMap<String, Double>();
		WilcoxonSignedRankTest wc = new WilcoxonSignedRankTest();
		double pvalue = wc.wilcoxonSignedRankTest(dummy1, dummy2, true);
		double wilcoxonScore = wc.wilcoxonSignedRank(dummy1, dummy2);
		result.put("pvalue", pvalue);
		result.put("testStatistic", wilcoxonScore);
		return result;
	}

	/**
	 * returns various statistical measures for an array of numbers.
	 * 
	 * @param numbers :double array consisting of the numbers.
	 * @return a HashMap containing.
	 * @throws Exception
	 */
	public HashMap<String, Double> getStatsForTheArray(double[] numbers) throws Exception {
		HashMap<String, Double> result = new HashMap<String, Double>();

		Skewness skew = new Skewness();
		Median medianObj = new Median();
		Kurtosis kurtosis = new Kurtosis();
		DescriptiveStatistics ds = new DescriptiveStatistics(numbers);

		double median = medianObj.evaluate(numbers);
		result.put("Minimum", ds.getMin());
		result.put("Maximum", ds.getMax());
		result.put("Range", ds.getMax() - ds.getMin());
		result.put("Mean", ds.getMean());
		result.put("Median", median);
		result.put("Mean-Median", ds.getMean() - median);
		result.put("StandardDeviation", ds.getStandardDeviation());
		result.put("SkewNess", skew.evaluate(numbers));

		double Q1 = ds.getPercentile(25);
		double Q3 = ds.getPercentile(75);
		double galton = (Q1 + Q3 - (2 * median)) / (Q3 - Q1);
		result.put("GaltonSkewness", galton);
		result.put("Kurtosis", kurtosis.evaluate(numbers));

		return result;
	}

}

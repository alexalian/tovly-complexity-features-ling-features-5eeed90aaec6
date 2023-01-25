/**
 * 
 */
package src.features;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import src.utils.genutils.NumUtils;

/**
 * Extracts features based on only words (e.g., TTRs, Frequency based features
 * etc.,) Uses Julia Hancke's code for MTLD.
 * 
 * @author svajjala
 */
public class WordBasedFeatures {

	public WordBasedFeatures() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/**
	 * Method to extract various word based features including type token ratios,
	 * and word frequency measures.
	 * 
	 * @param tokenizedSentences
	 * @return
	 * @throws Exception
	 */
	public TreeMap<String, Double> getWordBasedFeatures(ArrayList<String> tokenizedSentences) throws Exception {
		TreeMap<String, Double> wordBasedFeatures = new TreeMap<String, Double>();

		ArrayList<String> types = new ArrayList<String>();
		ArrayList<String> tokens = new ArrayList<String>();
		int numTokens = 0;
		for (String sentence : tokenizedSentences) {
			String[] wordsInSentence = sentence.split(" ");
			for (String word : wordsInSentence) {
				numTokens++;
				if (!types.contains(word.toLowerCase())) {
					types.add(word.toLowerCase());
				}
				tokens.add(word.toLowerCase());
			}
		} // All sentences iterated.

		double numTypes = (double) types.size();
		wordBasedFeatures.put("Word_TTR", NumUtils.handleDivByZero(numTypes, numTokens));
		wordBasedFeatures.put("Word_CTTR", NumUtils.handleDivByZero(numTypes, Math.sqrt(2.0 * numTokens)));
		wordBasedFeatures.put("Word_RTTR", NumUtils.handleDivByZero(numTypes, Math.sqrt(numTokens)));
		wordBasedFeatures.put("Word_BilogTTR", NumUtils.handleDivByZero(Math.log(numTypes), Math.log(numTokens)));
		wordBasedFeatures.put("Word_UberIndex",
				NumUtils.handleDivByZero(Math.pow(Math.log(numTokens), 2), Math.log(numTokens / numTypes)));
		wordBasedFeatures.put("Word_MTLD", NumUtils.restrict2TwoDecimals(getMTLD(tokens)));

		return wordBasedFeatures;
	}

	/**
	 * This uses Julia Hancke's version of MTLD with minimal changes.
	 * 
	 * @param tokens
	 * @return
	 * @throws Exception
	 */
	private static Double getMTLD(ArrayList<String> tokens) throws Exception {
		List<String> types = new ArrayList<String>();
		double factors = 0;
		double ttrThreshold = 0.72;
		int startIndex = 0;
		double ttr = 1;
		// go over the text and get ttr to get the number of factors
		for (int i = 0; i < tokens.size(); i++) {
			String currentToken = tokens.get(i);
			// each time a new type is found, compute type token ratio
			if (!types.contains(currentToken.toLowerCase())) {
				types.add(currentToken.toLowerCase());
			}
			ttr = types.size() / (i + 1 - startIndex);
			if (ttr < 0.72) {
				/*
				 * cut text (those portions are called factor) and reset list of types
				 */
				startIndex = i + 1;
				types.clear();
				// keep count of factors
				factors += 1;
			}
			/*
			 * if it is the last word and the ttr threshold is not reached, calculate the
			 * rest factor
			 */
			else if (ttr > ttrThreshold && i == tokens.size() - 1) {
				factors += (1 - ttr) / (1 - 0.72);
			}

		} // repeat until all tokens are finished.

		// form MTLD score: #tokens /#factors
		double mtld1 = tokens.size() / factors;

		// repeat same starting at the end of the text
		factors = 0;
		startIndex = tokens.size() - 1;
		ttr = 1;
		types.clear();
		for (int i = tokens.size() - 1; i >= 0; i--) {
			String currentToken = tokens.get(i);
			// each time a new type is found, compute type token ratio
			if (!types.contains(currentToken.toLowerCase())) {
				types.add(currentToken.toLowerCase());
			}
			// System.out.println("tokens covered " + (startIndex-i+1));
			ttr = types.size() / startIndex - i + 1;

			// when ttr reaches threshold
			if (ttr < ttrThreshold) {
				/* cut text (those portions are called factor) and reset list of types */
				startIndex = i - 1;
				types.clear();
				factors += 1;
			}
			/*
			 * if it is the last word and the ttr threshold is not reached calculate the
			 * rest factor
			 */
			else if (ttr > ttrThreshold && i == 0) {
				factors += (1 - ttr) / (1 - 0.72);
			}
		} // repeat until not tokens left

		double mtld2 = tokens.size() / factors;
		double res = (mtld1 + mtld2) / 2;
		Double resD = (Double) res;
		// take the mean of both forward and backward score
		if (!resD.isInfinite())
			return res;
		else
			return 0.0;
	}

}

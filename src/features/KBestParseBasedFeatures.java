/**
 * 
 */
package src.features;

import java.util.List;
import java.util.TreeMap;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.ScoredObject;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 * @author svajjala Generates Syntactic complexity features from Constituency
 *         trees. Extracts all the Lu (2010) features and a few more. Features
 *         are listed in the getSyntacticFeatures() method. Takes as input: list
 *         of parseTrees.
 */
public class KBestParseBasedFeatures {

	/**
	 * Extracts syntactic complexity features for a text
	 * 
	 * @param parsedSentences : List<Tree> object
	 * @return TreeMap object with feature name as key and its count as value.
	 * @throws Exception
	 */
	public TreeMap<String, Double> getSyntacticComplexityFeatures(List<List<ScoredObject<Tree>>> parsedSentences)
			throws Exception {
		TreeMap<String, Double> syntacticFeatures = new TreeMap<String, Double>();
		// List<Double> top2Diffs = new ArrayList<Double>();
		Double top2DiffTotal = 0.;
		Double topMeanDiffTotal = 0.;
		Double sdTotal = 0.;
		StandardDeviation sd = new StandardDeviation();
		for (List<ScoredObject<Tree>> scoredParses : parsedSentences) {
			if (scoredParses.size() > 1) {
				top2DiffTotal += scoredParses.get(0).score() - scoredParses.get(1).score();
			}
			double[] scores = scoredParses.stream().mapToDouble(sp -> sp.score()).toArray();
			Double avgScore = scoredParses.subList(0, scoredParses.size()).stream().mapToDouble(sp -> sp.score()).average()
					.orElse(0.);
			topMeanDiffTotal += scoredParses.get(0).score() - avgScore;
			sdTotal += sd.evaluate(scores);
		} // End of the for loop that finishes iterating through all parsed sentences in
			// the list.
		// Double numSentences = new Double((double) parsedSentences.size());
		syntacticFeatures.put("SYN_top2Diff", top2DiffTotal / parsedSentences.size());
		syntacticFeatures.put("SYN_topMeanDiff", topMeanDiffTotal / parsedSentences.size());
		syntacticFeatures.put("SYN_parseSD", sdTotal / parsedSentences.size());
		// syntacticFeatures.put("SYN_avgSentenceLength",
		// NumUtils.restrict2TwoDecimals(numWords / numSentences));
		// syntacticFeatures.put("SYN_MLC", NumUtils.handleDivByZero(numWords,
		// numClauses));

		return syntacticFeatures;
	}

}

/**
 * 
 */
package src.features;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.CollinsHeadFinder;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.ParseException;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.TregexPatternCompiler;

import src.utils.genutils.*;

/**
 * @author svajjala Generates Syntactic complexity features from Constituency
 *         trees. Extracts all the Lu (2010) features and a few more. Features
 *         are listed in the getSyntacticFeatures() method. Takes as input: list
 *         of parseTrees.
 */
public class ParseTreeBasedFeatures {

	public ParseTreeBasedFeatures() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// EXAMPLE STANDALONE USAGE:
		String text = "I could almost always tell when movies use fake dinosaurs.";
		ParseTreeBasedFeatures ptbf = new ParseTreeBasedFeatures();
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		LexicalizedParser lp = LexicalizedParser.loadModel("models/englishPCFG.ser.gz");
		List<Tree> sentences = new ArrayList<Tree>();
		for (List<HasWord> sentence : tokenizer) {
			sentences.add(lp.apply(sentence));
		}
		TreeMap<String, Double> eg = ptbf.getSyntacticComplexityFeatures(sentences);
		for (String s : eg.keySet()) // Prints all the features along with names.
		{
			System.out.println(s + "  " + eg.get(s));
		}

	}

	/**
	 * Extracts syntactic complexity features for a text
	 * 
	 * @param parsedSentences : List<Tree> object
	 * @return TreeMap object with feature name as key and its count as value.
	 * @throws Exception
	 */
	public TreeMap<String, Double> getSyntacticComplexityFeatures(List<Tree> parsedSentences) throws Exception {
		TreeMap<String, Double> syntacticFeatures = new TreeMap<String, Double>();
		// Declare a few variables:
		int numSBAR = 0;
		int avgParseTreeHeight = 0;
		int numNP = 0;
		int numVP = 0;
		int numPP = 0;
		// int distHeadWord = 0; //Average index of the headword's position in a
		// sentence
		int numConstituents = 0; // Number of Constituents of a Parse Tree. Although I have no clue what they
															// mean by a constituent..I am trying.
		int numSubtrees = 0;
		// int distSemanticHeadWord = 0;
		int numWhPhrases = 0;
		int numConjPhrases = 0;
		int reducedRelClauses = 0;

		int numWords = 0;

		int numClauses = 0;
		int numTunits = 0;
		int numComplexNominals = 0;
		int numDependentClauses = 0;
		int numCoordinateClauses = 0;
		int numComplexTunits = 0;

		int AvgNPSize = 0;
		int AvgVPSize = 0;
		int AvgPPSize = 0;

		HeadFinder hf = new CollinsHeadFinder();

		for (Tree t : parsedSentences) {
			// System.out.println(t.toString());
			avgParseTreeHeight += t.depth();
			numWords += t.getLeaves().size(); // NumWords in this sentence.

			// Calculate Num. NP, VP, PP and their Average Sizes. Tregex Patterns are not
			// necessary for this part.
			List<edu.stanford.nlp.trees.Tree> subtrees = t.subTreeList();
			numSubtrees += subtrees.size();
			for (edu.stanford.nlp.trees.Tree st : subtrees) {
				if (st.isPhrasal() && st.headTerminal(hf) != null) {
					if (st.label().toString().equals("NP") && st.isPrePreTerminal()) {
						numNP++;
						AvgNPSize += st.numChildren();
					}

					if (st.label().toString().equals("VP")) {
						numVP++;
						AvgVPSize += st.numChildren();
					}

					if (st.label().toString().equals("PP")) {
						numPP++;
						AvgPPSize += st.numChildren();
					}

					if (st.label().toString().equals("WHNP") || st.label().toString().equals("WHPP")
							|| st.label().toString().equals("WHADVP") || st.label().toString().equals("WHADJP")) {
						numWhPhrases++;
					}

					if (st.label().toString().equals("RRC")) {
						reducedRelClauses++;
					}

					if (st.label().toString().equals("CONJP")) {
						numConjPhrases++;
					}
				}
			} // End of for loop iterating through the subtrees inside a given tree.

			// The Tregex Mania begins! For details about these patterns, see Lu-10.
			numSBAR = numSBAR + countOccurences(t, "SBAR");
			numClauses = numClauses + countOccurences(t, "S|SBAR|SINV < (VP <# VBD|VBP|VBZ|MD)");
			numTunits = numTunits
					+ countOccurences(t, "S|SBARQ|SINV|SQ !> (S|SINV|SBAR|SQ) |> ROOT | [$-- S|SBARQ|SINV|SQ !>> SBAR|VP]");
			numDependentClauses = numDependentClauses
					+ countOccurences(t, "SBAR < (S|SINV|SQ < (VP [<# MD|VBP|VBZ|VBD | < (VP <# (MD|VBP|VBZ|VBD))]))");
			numCoordinateClauses = numCoordinateClauses + countOccurences(t, "ADJP|ADVP|NP|VP<CC");
			numComplexTunits = numComplexTunits + countOccurences(t,
					"S|SBARQ|SINV|SQ [> ROOT | [$-- S|SBARQ|SINV|SQ !>> SBAR|VP]] << (SBAR < (S|SINV|SQ < (VP [<# MD|VBP|VBZ|VBD | < (VP <# (MD|VBP|VBZ|VBD))])))");
			// Three CN patterns.
			numComplexNominals = numComplexNominals
					+ countOccurences(t, "NP !> NP [<< JJ|POS|PP|S|VBG | << (NP $++ NP !$+ CC)]");
			numComplexNominals = numComplexNominals
					+ countOccurences(t, "SBAR [<# WHNP | <# (IN < That|that|For|for) | <, S] & [$+ VP | > VP]");
			numComplexNominals = numComplexNominals + countOccurences(t, "S < (VP <# VBG|TO) $+ VP");

		} // End of the for loop that finishes iterating through all parsed sentences in
			// the list.

		Double numSentences = new Double((double) parsedSentences.size());
		syntacticFeatures.put("SYN_numSentences", numSentences);
		// syntacticFeatures.put("numWords", (Double)((double)numWords));
		syntacticFeatures.put("SYN_avgSentenceLength", NumUtils.restrict2TwoDecimals(numWords / numSentences));
		syntacticFeatures.put("SYN_MLC", NumUtils.handleDivByZero(numWords, numClauses));
		syntacticFeatures.put("SYN_MLT", NumUtils.handleDivByZero(numWords, numTunits));
		syntacticFeatures.put("SYN_avgParseTreeHeightPerSen",
				NumUtils.restrict2TwoDecimals(avgParseTreeHeight / numSentences));
		syntacticFeatures.put("SYN_numSubtreesPerSen", NumUtils.restrict2TwoDecimals(numSubtrees / numSentences));
		syntacticFeatures.put("SYN_numConstituentsPerSen", NumUtils.restrict2TwoDecimals(numConstituents / numSentences));
		syntacticFeatures.put("SYN_numSBARsPerSen", NumUtils.restrict2TwoDecimals(numSBAR / numSentences));
		syntacticFeatures.put("SYN_numNPsPerSen", NumUtils.restrict2TwoDecimals(numNP / numSentences));
		syntacticFeatures.put("SYN_numVPsPerSen", NumUtils.restrict2TwoDecimals(numVP / numSentences));
		syntacticFeatures.put("SYN_numPPsPerSen", NumUtils.restrict2TwoDecimals(numPP / numSentences));
		syntacticFeatures.put("SYN_numNPSize", NumUtils.handleDivByZero(AvgNPSize, numNP));
		syntacticFeatures.put("SYN_numVPSize", NumUtils.handleDivByZero(AvgVPSize, numVP));
		syntacticFeatures.put("SYN_numPPSize", NumUtils.handleDivByZero(AvgPPSize, numPP));
		syntacticFeatures.put("SYN_numWHPsPerSen", NumUtils.restrict2TwoDecimals(numWhPhrases / numSentences));
		syntacticFeatures.put("SYN_numRRCsPerSen", NumUtils.restrict2TwoDecimals(reducedRelClauses / numSentences));
		syntacticFeatures.put("SYN_numConjPPerSen", NumUtils.restrict2TwoDecimals(numConjPhrases / numSentences));
		syntacticFeatures.put("SYN_numClausesPerSen", NumUtils.restrict2TwoDecimals(numClauses / numSentences));
		syntacticFeatures.put("SYN_numTunitsPerSen", NumUtils.restrict2TwoDecimals(numTunits / numSentences));
		syntacticFeatures.put("SYN_TunitComplexityRatio", NumUtils.handleDivByZero(numClauses, numTunits));
		syntacticFeatures.put("SYN_ComplexTunitRatio", NumUtils.handleDivByZero(numComplexTunits, numTunits));
		syntacticFeatures.put("SYN_DependentClauseRatio", NumUtils.handleDivByZero(numDependentClauses, numClauses));
		syntacticFeatures.put("SYN_DependentClausesPerTunit", NumUtils.handleDivByZero(numDependentClauses, numTunits));
		syntacticFeatures.put("SYN_CoordPerClause", NumUtils.handleDivByZero(numCoordinateClauses, numClauses));
		syntacticFeatures.put("SYN_CoordPerTunit", NumUtils.handleDivByZero(numCoordinateClauses, numTunits));
		syntacticFeatures.put("SYN_CNPerClause", NumUtils.handleDivByZero(numComplexNominals, numClauses));
		syntacticFeatures.put("SYN_CNPerTunit", NumUtils.handleDivByZero(numComplexNominals, numTunits));
		syntacticFeatures.put("SYN_VPPerTunit", NumUtils.handleDivByZero(numVP, numTunits));

		return syntacticFeatures;
	}

	/**
	 * Counts the occurences of a given pattern in a tree. Uses a tregexpattern
	 * matcher.
	 * 
	 * @param tree    - a Stanford tree
	 * @param pattern - a tregex pattern
	 * @return a count (as an integer)
	 * @throws ParseException
	 */
	private static int countOccurences(edu.stanford.nlp.trees.Tree tree, String pattern) throws ParseException {
		TregexPatternCompiler tpc = new TregexPatternCompiler();
		TregexPattern p0 = tpc.compile(pattern);
		TregexMatcher m0 = p0.matcher(tree);
		int count = 0;
		while (m0.find()) {
			count++;
		}
		return count;
	}

}

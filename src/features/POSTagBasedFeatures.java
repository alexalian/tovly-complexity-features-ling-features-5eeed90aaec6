/**
 * 
 */
package src.features;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import src.utils.genutils.*;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;

/**
 * Calculates features that are based on POS tag representations.
 * 
 * @author svajjala
 */
public class POSTagBasedFeatures {

	public POSTagBasedFeatures() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// EXAMPLE STANDALONE USAGE:
		String text = "I could almost always tell when movies use fake dinosaurs.";
		POSTagBasedFeatures posf = new POSTagBasedFeatures();
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		MaxentTagger tagger = new MaxentTagger("models/english-left3words-distsim.tagger");
		List<List<TaggedWord>> sentences = new ArrayList<List<TaggedWord>>();
		for (List<HasWord> sentence : tokenizer) {
			sentences.add(tagger.tagSentence(sentence));
		}
		TreeMap<String, Double> eg = posf.getPOSTagBasedFeatures(sentences);
		for (String s : eg.keySet()) // Prints all the features along with names.
		{
			System.out.println(s + "  " + eg.get(s));
		}
	}

	/**
	 * Method to calculate POS tag ratio features
	 * 
	 * @param taggedSentences - list of tagged sentences
	 * @return TreeMap object with feature names and values.
	 * @throws Exception
	 */
	public TreeMap<String, Double> getPOSTagBasedFeatures(List<List<TaggedWord>> taggedSentences) throws Exception {

		TreeMap<String, Double> posFeatures = new TreeMap<String, Double>();
		int TotalWords = 0;
		int numAdj = 0;
		int numNouns = 0;
		int numVerbs = 0;
		int numPronouns = 0;
		int numConjunct = 0;
		int numProperNouns = 0;
		int numPrepositions = 0;
		int numAdverbs = 0;
		int numLexicals = 0;
		int numModals = 0;
		int numInterjections = 0;
		int perpronouns = 0; // adding num Personal Pronouns with the hypothesis that they will occur more in
													// Simple Sentences
		int whperpronouns = 0; // adding num Wh personal pronouns with the hypothesis that they will occur more
														// in Normal sentences.
		int numauxverbs = 0;
		int numFunctionWords = 0; // by Wiki: Articles, Pronouns, Conjunctions, Interjections, Prep, Adverbs,
															// Aux-Verbs.
		int numDeterminers = 0;
		// int numTenses = 0; //Number of different tenses in the sentence. Intuition:
		// More tenses, more difficult to understand.
		int numVB = 0;
		int numVBD = 0;
		int numVBG = 0;
		int numVBN = 0;
		int numVBP = 0;
		int numVBZ = 0;

		ArrayList<String> uniqueVerbs = new ArrayList<String>();
		TreeMap<String, Integer> tagCounts = new TreeMap<String, Integer>();
		Counter<String> tagCounter = new ClassicCounter<String>();
		for (List<TaggedWord> sentence : taggedSentences) {
			for (TaggedWord wordtag : sentence) {
				String tag = wordtag.tag();
				String word = wordtag.word();
				Integer tagCount = tagCounts.getOrDefault(tag, 0);
				tagCounts.put(tag, tagCount + 1);
				tagCounter.incrementCount(tag);
				if (tag.equals("PRP") || tag.equals("PRP$") || tag.equals("WP") || tag.equals("WP$")) {
					numPronouns++;
					if (tag.equals("PRP")) {
						perpronouns++;
					}
					if (tag.equals("WP")) {
						whperpronouns++;
					}
					numFunctionWords++;
					TotalWords++;
				}
				if (tag.equals("VB") || tag.equals("VBD") || tag.equals("VBG") || tag.equals("VBN") || tag.equals("VBP")
						|| tag.equals("VBZ")) {
					numVerbs++;
					TotalWords++;
					if (!uniqueVerbs.contains(word)) {
						uniqueVerbs.add(word);
					}
					if (tag.equals("VB")) {
						numVB++;
					} else if (tag.equals("VBD")) {
						numVBD++;
					} else if (tag.equals("VBG")) {
						numVBG++;
					} else if (tag.equals("VBN")) {
						numVBN++;
					}
					if (tag.equals("VBP")) {
						numVBP++;
					}
					if (tag.equals("VBZ")) {
						numVBZ++;
					}
				}
				if (tag.equals("JJ") || tag.equals("JJR") || tag.equals("JJS")) {
					numAdj++;
					TotalWords++;
				}
				if (tag.equals("RB") || tag.equals("RBR") || tag.equals("RBS") || tag.equals("RP")) {
					numAdverbs++;
					numFunctionWords++;
					TotalWords++;
				}
				if (tag.equals("IN")) {
					numPrepositions++;
					numFunctionWords++;
					TotalWords++;
				}
				if (tag.equals("UH")) {
					numInterjections++;
					numFunctionWords++;
					TotalWords++;
				}
				if (tag.equals("CC")) {
					numConjunct++;
					numFunctionWords++;
					TotalWords++;
				}
				if (tag.equals("NN") || tag.equals("NNS")) {

					numNouns++;
					TotalWords++;
				}
				if (tag.equals("NNP") || tag.equals("NNPS")) {
					numProperNouns++;
					TotalWords++;
				}
				if (tag.equals("MD")) {
					numModals++;
					numauxverbs++;
					numFunctionWords++;
					TotalWords++;
				}
				if (tag.equals("DT")) {
					numFunctionWords++;
					numDeterminers++;
					TotalWords++;
				}

			} // End of all words in a sentence.
		} // End of all sentences
		Double totalDiv = 0.;
		for (List<TaggedWord> sentence : taggedSentences) {
			Counter<String> sentTagCount = new ClassicCounter<String>();
			for (TaggedWord wordtag : sentence) {
				sentTagCount.incrementCount(wordtag.tag());
			}
			totalDiv += Counters.klDivergence(sentTagCount, tagCounter);
		}
		numLexicals += numAdj + numNouns + numVerbs + numAdverbs + numProperNouns; // Lex.Den = NumLexicals/TotalWords
		int numVerbsOnly = numVerbs - numauxverbs;
		double[] tagCountsArray = tagCounts.values().stream().mapToDouble(x -> x).toArray();
		// Integer[] tagCountsArray = tagCountsCollection.toArray(new
		// Integer[tagCountsCollection.size()]);
		StandardDeviation sd = new StandardDeviation();
		// Index index = new Index();
		// tagCounts.keySet().stream().forEach(x -> index.addToIndex(x));
		// Counter<double> docTagCounts = Counters.toCounter(tagCountsArray, index);
		// Distribution tagDist = Distribution()

		posFeatures.put("POS_tagSD", NumUtils.restrict2TwoDecimals(sd.evaluate(tagCountsArray)));
		posFeatures.put("POS_avgSenKLDiv", NumUtils.restrict2TwoDecimals((double) (totalDiv) / taggedSentences.size()));
		posFeatures.put("POS_numNouns", NumUtils.restrict2TwoDecimals((double) (numNouns + numProperNouns) / TotalWords));
		posFeatures.put("POS_numProperNouns", NumUtils.restrict2TwoDecimals((double) numProperNouns / TotalWords));
		posFeatures.put("POS_numPronouns", NumUtils.restrict2TwoDecimals((double) (numPronouns) / TotalWords));
		posFeatures.put("POS_numConjunct", NumUtils.restrict2TwoDecimals((double) (numConjunct) / TotalWords));
		posFeatures.put("POS_numAdjectives", NumUtils.restrict2TwoDecimals((double) (numAdj) / TotalWords));
		posFeatures.put("POS_numVerbs", NumUtils.restrict2TwoDecimals((double) (numVerbs) / TotalWords));
		posFeatures.put("POS_numAdverbs", NumUtils.restrict2TwoDecimals((double) (numAdverbs) / TotalWords));
		posFeatures.put("POS_numModals", NumUtils.restrict2TwoDecimals((double) numModals / TotalWords));
		posFeatures.put("POS_numPrepositions", NumUtils.restrict2TwoDecimals((double) numPrepositions / TotalWords));
		posFeatures.put("POS_numInterjections", NumUtils.restrict2TwoDecimals((double) numInterjections / TotalWords));
		posFeatures.put("POS_numPerPronouns", NumUtils.restrict2TwoDecimals((double) perpronouns / TotalWords));
		posFeatures.put("POS_numWhPronouns", NumUtils.restrict2TwoDecimals((double) whperpronouns / TotalWords));
		posFeatures.put("POS_numLexicals", NumUtils.restrict2TwoDecimals((double) (numLexicals) / TotalWords)); // Lexical
																																																						// Density
		posFeatures.put("POS_numFunctionWords", NumUtils.restrict2TwoDecimals((double) (numFunctionWords) / TotalWords));
		posFeatures.put("POS_numDeterminers", NumUtils.restrict2TwoDecimals((double) (numDeterminers) / TotalWords));
		posFeatures.put("POS_numVerbsVB", NumUtils.restrict2TwoDecimals((double) (numVB) / TotalWords));
		posFeatures.put("POS_numVerbsVBD", NumUtils.restrict2TwoDecimals((double) (numVBD) / TotalWords));
		posFeatures.put("POS_numVerbsVBG", NumUtils.restrict2TwoDecimals((double) (numVBG) / TotalWords));
		posFeatures.put("POS_numVerbsVBN", NumUtils.restrict2TwoDecimals((double) (numVBN) / TotalWords));
		posFeatures.put("POS_numVerbsVBP", NumUtils.restrict2TwoDecimals((double) (numVBP) / TotalWords));
		posFeatures.put("POS_numVerbsVBZ", NumUtils.restrict2TwoDecimals((double) (numVBZ) / TotalWords));
		posFeatures.put("POS_advVar", NumUtils.restrict2TwoDecimals((double) numAdverbs / numLexicals));
		posFeatures.put("POS_adjVar", NumUtils.restrict2TwoDecimals((double) numAdj / numLexicals));
		posFeatures.put("POS_modVar", NumUtils.restrict2TwoDecimals((double) (numAdj + numAdverbs) / numLexicals));
		posFeatures.put("POS_nounVar", NumUtils.restrict2TwoDecimals((double) (numNouns + numProperNouns) / numLexicals));
		posFeatures.put("POS_verbVar1", NumUtils.restrict2TwoDecimals((double) (numVerbsOnly) / uniqueVerbs.size())); // VV1
		posFeatures.put("POS_verbVar2", NumUtils.restrict2TwoDecimals((double) (numVerbsOnly) / numLexicals)); // VV2
		posFeatures.put("POS_squaredVerbVar1",
				NumUtils.restrict2TwoDecimals((double) (numVerbsOnly * numVerbsOnly) / uniqueVerbs.size())); // VV1
		posFeatures.put("POS_correctedVV1",
				NumUtils.restrict2TwoDecimals((double) (numVerbsOnly) / Math.sqrt(2.0 * uniqueVerbs.size()))); // CVV1

		return posFeatures;
	}
}

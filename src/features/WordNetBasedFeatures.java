/**
 * 
 */
package src.features;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.ling.TaggedWord;

import src.utils.genutils.*;

/**
 * Extracts some features from WordNet using Wordnet API. Updated the API
 * version to 2.3.3 in Aug 2015.
 * 
 * @author svajjala Previous version used only one feature: numSenses. I added
 *         numHypernyms and numHyponyms for the first meaning of the word. To
 *         add more, one can look at the documentation about the API:
 *         http://projects.csail.mit.edu/jwi/download.php?f=edu.mit.jwi_2.3.3_manual.pdf
 */
public class WordNetBasedFeatures {

	// Variables for WN
	public static URL wnpath = null;
	public static IDictionary dict = null;
	public static WordnetStemmer stemmer = null;
	public static List<String> auxVerbs = null;

	public WordNetBasedFeatures() throws Exception {
		init();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/**
	 * Method to get wordnet features.
	 * 
	 * @param taggedSentences - list of tagged sentences.
	 * @return TreeMap object containing features and their values.
	 * @throws Exception
	 */
	public TreeMap<String, Double> getWNFeatures(List<List<TaggedWord>> taggedSentences) throws Exception {
		TreeMap<String, Double> wnfeatures = new TreeMap<String, Double>();
		int numSenses = 0;
		int wordsforwhichsensesarecounted = 0;
		int numHypernyms = 0;
		int numHyponyms = 0;
		for (List<TaggedWord> sentence : taggedSentences) {
			for (TaggedWord wordtag : sentence) {
				String word = wordtag.word().toLowerCase();
				String tag = wordtag.tag();
				String generaltag = getGeneralTag(tag);
				if (!generaltag.equals("NONE")) {
					try {
						String lemma = stemmer.findStems(word, POS.valueOf(generaltag)).get(0);
						if (!auxVerbs.contains(lemma) && !generaltag.equals("PROPERNOUN")) // Calculate no. of senses only if the
																																								// word is not an aux. verb.
						{
							IIndexWord idxWord = dict.getIndexWord(lemma, POS.valueOf(generaltag));
							numSenses += idxWord.getWordIDs().size(); // Senses of a word.

							// Getting hypernyms:
							IWordID wordID = idxWord.getWordIDs().get(0); // 1st meaning of the idXWord.
							IWord iword = dict.getWord(wordID);
							ISynset synset = iword.getSynset();
							numHypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM).size();
							numHyponyms = synset.getRelatedSynsets(Pointer.HYPONYM).size();
							wordsforwhichsensesarecounted++;
						}
					} catch (Exception wnException) {
						// System.out.println("Error while handling Wordnet: " +
						// wnException.toString());
						// continue;
					}
				}
			}
		}
		wnfeatures.put("WN_numSenses", NumUtils.handleDivByZero((double) numSenses, wordsforwhichsensesarecounted));
		wnfeatures.put("WN_numHypernyms", NumUtils.handleDivByZero((double) numHypernyms, wordsforwhichsensesarecounted));
		wnfeatures.put("WN_numHyponyms", NumUtils.handleDivByZero((double) numHyponyms, wordsforwhichsensesarecounted));
		return wnfeatures;
	}

	/**
	 * Method to load WN database and also initialize a list of auxverbs.
	 * 
	 * @throws Exception
	 */
	private static void init() throws Exception {
		// Wordnet related initializations
		wnpath = prepareWNUrl();
		// construct the dictionary object and open it
		dict = new Dictionary(wnpath);
		dict.open();
		stemmer = new WordnetStemmer(dict);
		// Populating the list of Auxiliary verbs, for Wordnet related stuff.
		String[] auxverbslist = { "be", "can", "could", "do", "have", "may", "might", "must", "shall", "should", "will",
				"would" };
		auxVerbs = Arrays.asList(auxverbslist);

	}

	/**
	 * Method to prepare WN path as expected by the Dictionary object.
	 * 
	 * @return
	 */
	private static URL prepareWNUrl() {
		String wnpath = "resources/dict";
		URL url = null;
		try {
			url = new URL("file", null, wnpath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * Get a more general tag for POS tags, as WN expects only these four tags
	 * 
	 * @param tag specific tag
	 * @return string - general tag.
	 * @throws Exception
	 */
	private static String getGeneralTag(String tag) throws Exception {
		String generalTag = "NONE";
		if (tag.startsWith("VB")) {
			generalTag = "VERB";
		} else if (tag.startsWith("JJ")) {
			generalTag = "ADJECTIVE";
		} else if (tag.startsWith("RB") || tag.equals("RP")) {
			generalTag = "ADVERB";
		} else if (tag.equals("NN") || tag.equals("NNS")) {
			generalTag = "NOUN";
		} else if (tag.equals("NNP") || tag.equals("NNPS")) {
			generalTag = "PROPERNOUN";
		}
		return generalTag;
	}

}

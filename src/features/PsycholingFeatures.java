/**
 * 
 */
package src.features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import src.utils.genutils.*;

/**
 * Extracts Psycholinguistic features and AoA features. Uses MRC and AoA scores
 * from Kuperman et.al. (2012) paper.
 * 
 * @author svajjala
 * 
 *         This does not even require a POS tag. I wonder why I was using a POS
 *         tagged version all the while!
 */
public class PsycholingFeatures {

	/**
	 * 
	 */
	private static Hashtable<String, String> aoaDb = new Hashtable<String, String>(100000);
	private static Hashtable<String, String> mrcdb = new Hashtable<String, String>(200000);

	public PsycholingFeatures() throws Exception {

		init(); // Loads the
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Example usage:
		String text = "I could almost always tell when movies use fake dinosaurs.";
		MaxentTagger tagger = new MaxentTagger("models/english-left3words-distsim.tagger");
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		List<List<TaggedWord>> taggedSentences = new ArrayList<List<TaggedWord>>();
		for (List<HasWord> sentence : tokenizer) {
			taggedSentences.add(tagger.tagSentence(sentence));
		}
		System.out.println(taggedSentences.size());
		PsycholingFeatures psycho = new PsycholingFeatures();
		TreeMap<String, Double> eg = psycho.getPsycholingFeatures(taggedSentences);
		for (String s : eg.keySet()) // Prints all the features along with names.
		{
			System.out.println(s + "  " + eg.get(s));
		}
	}

	/**
	 * Extracts Psycholinguistic features based on Kuperman et.al. database and MRC
	 * psycholinguistic database
	 * 
	 * @param taggedSentences - List of tagged sentences (in Stanford TaggedWord
	 *                        format) (This perhaps will not even need tagged
	 *                        sentences. Tokenized sentences will do.)
	 * @return TreeMap object containing the feature names and values.
	 * @throws Exception
	 */
	public TreeMap<String, Double> getPsycholingFeatures(List<List<TaggedWord>> taggedSentences) throws Exception {
		TreeMap<String, Double> psycholingFeatures = new TreeMap<String, Double>();
		// Declare all the necessary number variables:
		// From Aoa: AoA_Kup,AoA_kup_Lem,AoA_Bird_Lem,AoA_Bristol_Lem,AoA_Cort_Lem
		double AoA_Kup = 0.0;
		double AoA_Kup_Lem = 0.0;
		double AoA_Bird_Lem = 0.0;
		double AoA_Bristol_Lem = 0.0;
		double AoA_Cort_Lem = 0.0;

		// From MRC: familiarity,concreteness,imagery,colorado meaningfulness,pavio
		// meaningfulness,AoA
		double MrcFam = 0.0;
		double MrcConc = 0.0;
		double MrcImag = 0.0;
		double MrcColMean = 0.0;
		double MrcPavioMean = 0.0;
		double MrcAoA = 0.0;

		// A variable to count the number of words that got all these characteristics.
		// Useful to estimate the average.
		int numAoAwords = 0;
		int numMrcWords = 0;

		for (List<TaggedWord> taggedSentence : taggedSentences) {
			for (TaggedWord wordTagPair : taggedSentence) {
				String word = wordTagPair.word();
				if (aoaDb.containsKey(word.toLowerCase())) {
					numAoAwords++;
					String[] variousaoavalues = aoaDb.get(word.toLowerCase()).toString().replaceAll("none", "0")
							.replaceAll("NA", "0").split(",");

					AoA_Kup += Double.parseDouble(variousaoavalues[0]);
					AoA_Kup_Lem += Double.parseDouble(variousaoavalues[1]);
					AoA_Bird_Lem += Double.parseDouble(variousaoavalues[2]);
					AoA_Bristol_Lem += Double.parseDouble(variousaoavalues[3]);
					AoA_Cort_Lem += Double.parseDouble(variousaoavalues[4]);
				}
				// familiarity,concreteness,imagery,colorado meaningfulness,pavio
				// meaningfulness,AoA
				if (mrcdb.containsKey(word.toLowerCase())) {
					numMrcWords++;
					String[] variousmrcvalues = mrcdb.get(word.toLowerCase()).toString().replaceAll("none", "0")
							.replaceAll("NA", "0").split(",");

					/*
					 * I am not sure what the denominator should be. Previously (in the thesis
					 * version), it was 7, because they mentioned in a documentation that its on a
					 * scale of 1 to 7. But there are actually quite large values. So, I am now
					 * dividing everything by 100 - Sowmya.
					 */
					MrcFam += Double.parseDouble(variousmrcvalues[0]) / 100;
					MrcConc += Double.parseDouble(variousmrcvalues[1]) / 100;
					MrcImag += Double.parseDouble(variousmrcvalues[2]) / 100;
					MrcColMean += Double.parseDouble(variousmrcvalues[3]) / 100;
					MrcPavioMean += Double.parseDouble(variousmrcvalues[4]) / 100;
					MrcAoA += Double.parseDouble(variousmrcvalues[5]) / 100;

				}
			} // End of the for loop for all words in a sentence
		} // End of the for loop for all sentences in the text.

		psycholingFeatures.put("AoA_Kup", NumUtils.restrict2TwoDecimals((double) AoA_Kup / numAoAwords));
		psycholingFeatures.put("AoA_Kup_Lem", NumUtils.restrict2TwoDecimals((double) AoA_Kup_Lem / numAoAwords));
		psycholingFeatures.put("AoA_Bird_Lem", NumUtils.restrict2TwoDecimals((double) AoA_Bird_Lem / numAoAwords));
		psycholingFeatures.put("AoA_Bristol_Lem", NumUtils.restrict2TwoDecimals((double) AoA_Bristol_Lem / numAoAwords));
		psycholingFeatures.put("AoA_Cort_Lem", NumUtils.restrict2TwoDecimals((double) AoA_Cort_Lem / numAoAwords));

		psycholingFeatures.put("MRCFamiliarity", NumUtils.restrict2TwoDecimals((double) MrcFam / numMrcWords));
		psycholingFeatures.put("MRCConcreteness", NumUtils.restrict2TwoDecimals((double) MrcConc / numMrcWords));
		psycholingFeatures.put("MRCImageability", NumUtils.restrict2TwoDecimals((double) MrcImag / numMrcWords));
		psycholingFeatures.put("MRCColMeaningfulness", NumUtils.restrict2TwoDecimals((double) MrcColMean / numMrcWords));
		psycholingFeatures.put("MRCPavioMeaningfulness",
				NumUtils.restrict2TwoDecimals((double) MrcPavioMean / numMrcWords));
		psycholingFeatures.put("MRCAoA", NumUtils.restrict2TwoDecimals((double) MrcAoA / numMrcWords));

		return psycholingFeatures;
	}

	/**
	 * Method to load AoA and MRC databases.
	 * 
	 * @throws Exception
	 */
	private static void init() throws Exception {
		String aoa50Kpath = "resources/AoA_51715_words_copy.csv";
		String mrcfilepath = "resources/mrcdictfull_copy.csv";

		BufferedReader br = new BufferedReader(new FileReader(aoa50Kpath));
		br.readLine(); // To read the header.

		// Load the AoA database
		String dummy = "";
		while ((dummy = br.readLine()) != null) {
			try {
				String[] pair = dummy.split(",");
				String key = pair[0];
				String value = pair[8] + "," + pair[10] + "," + pair[12] + "," + pair[13] + "," + pair[14]; // +","+pair[15];
				aoaDb.put(key, value); // End value string: "word":
																// AoA_Kup,AoA_kup_Lem,AoA_Bird_Lem,AoA_Bristol_Lem,AoA_Cort_Lem,
			} catch (Exception e) {
				System.out.println("Something is wrong with your AoA database format. Exiting. " + e.toString());
				System.exit(1);
			}
		}
		br.close();

		// Load MRC Psycholinguistic Database
		br = new BufferedReader(new FileReader(mrcfilepath));
		while ((dummy = br.readLine()) != null) {
			try {
				String[] pair = dummy.split(",");
				String key = pair[22].toLowerCase();
				String value = pair[8] + "," + pair[9] + "," + pair[10] + "," + pair[11] + "," + pair[12] + "," + pair[13];
				if (!mrcdb.containsKey(key)) {
					mrcdb.put(key, value); // End value string: familiarity,concreteness,imagery,colorado
																	// meaningfulness,pavio meaningfulness,AoA

				}
			} catch (Exception e) {
				System.out.println("Something is wrong with your MRC database format. Exiting. " + e.toString());
				System.exit(1);
			}
		}
		br.close();

		System.out.println(aoaDb.size() + "...AoA DB size");
		System.out.println(mrcdb.size() + "....MRD Db size");
	}

}

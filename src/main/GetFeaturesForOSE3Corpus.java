/**
 * 
 */
package src.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.Tree;
import src.features.POSTagBasedFeatures;
import src.features.ParseTreeBasedFeatures;
import src.features.PsycholingFeatures;
import src.features.TraditionalFeatures;
import src.features.WordBasedFeatures;
import src.features.WordNetBasedFeatures;
import src.preprocessing.PreprocessText;

/**
 * Feature extraction for the OSE3 corpus. SVMLight format.
 * 
 * @author svajjala
 * 
 *         Prints the output as a series of Feature, Value strings.
 */
public class GetFeaturesForOSE3Corpus {

	/**
	 * @param args
	 */
	private static WordBasedFeatures wordbased;
	private static TraditionalFeatures tradfeatures;
	private static POSTagBasedFeatures postagfeatures;
	private static PsycholingFeatures psycholingfeatures;
	private static ParseTreeBasedFeatures parsetreefeatures;
	private static PreprocessText process;

	public static void main(String[] args) throws Exception {

		if (!(args.length == 2)) {
			System.out.println(
					"Two arguments. Args1 is the path to OSE3 sentence aligned corpus. Args2 is the path to save feature file");
			System.exit(1);
		}
		init();
		String inputPath = args[0];
		String outputPath = args[1];

		BufferedReader br = new BufferedReader(new FileReader(inputPath));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));

		String dummy = "";
		int qid = 1;
		ArrayList<String> temp = new ArrayList<String>();
		while ((dummy = br.readLine()) != null) {
			if (!(dummy.contains("****"))) {
				temp.add(dummy);
			} else {
				if (temp.size() == 3) {
					for (int i = 0; i < temp.size(); i++) {
						int level = 3 - i;
						@SuppressWarnings("unchecked")
						List<?> preprocessed = (List<Tree>) process.preProcessFile(temp.get(i));
						@SuppressWarnings("unchecked")
						List<Tree> parsedSentence = (List<Tree>) preprocessed.get(1);
						@SuppressWarnings("unchecked")
						List<List<TaggedWord>> taggedSentence = (List<List<TaggedWord>>) preprocessed.get(0);
						@SuppressWarnings("unchecked")
						ArrayList<String> tokenizedSentence = (ArrayList<String>) preprocessed.get(2);

						TreeMap<String, Double> allFeatures = new TreeMap<String, Double>();
						allFeatures.putAll(getTradFeatures(temp.get(i)));
						allFeatures.putAll(getWordBasedFeatures(tokenizedSentence));
						allFeatures.putAll(getPOSFeatures(taggedSentence));
						allFeatures.putAll(getPsychFeatures(taggedSentence));
						allFeatures.putAll(getParseTreeFeatures(parsedSentence));
						String result = "";
						int index = 1;
						for (String s : allFeatures.keySet()) {
							result += index + ":" + allFeatures.get(s) + " ";
							index++;
						}
						// System.exit(1);
						bw.write(level + " " + "qid:" + qid + " " + result);
						bw.newLine();
					}
					temp.clear();
					qid = qid + 1;
				} else {
					System.out.println("Something is wrong!!" + temp.get(0));
				}
			}
		}
		bw.flush();
		bw.close();
		br.close();
		System.out.println("Wrote feature file for " + qid + " triplets");
	}

	private static TreeMap<String, Double> getTradFeatures(String sentence) throws Exception {
		TreeMap<String, Double> result = tradfeatures.getTraditionalFeatures(sentence);
		return result;
	}

	private static TreeMap<String, Double> getWordBasedFeatures(ArrayList<String> tokenizedSentence) throws Exception {
		TreeMap<String, Double> result = wordbased.getWordBasedFeatures(tokenizedSentence);
		return result;
	}

	private static TreeMap<String, Double> getParseTreeFeatures(List<Tree> parsedSentence) throws Exception {
		TreeMap<String, Double> result = parsetreefeatures.getSyntacticComplexityFeatures(parsedSentence);
		return result;
	}

	private static TreeMap<String, Double> getPOSFeatures(List<List<TaggedWord>> taggedSentence) throws Exception {
		TreeMap<String, Double> result = postagfeatures.getPOSTagBasedFeatures(taggedSentence);
		return result;
	}

	private static TreeMap<String, Double> getPsychFeatures(List<List<TaggedWord>> taggedSentence) throws Exception {
		TreeMap<String, Double> result = psycholingfeatures.getPsycholingFeatures(taggedSentence);
		return result;
	}

	private static void init() throws Exception {
		wordbased = new WordBasedFeatures();
		tradfeatures = new TraditionalFeatures();
		postagfeatures = new POSTagBasedFeatures();
		new WordNetBasedFeatures();
		psycholingfeatures = new PsycholingFeatures();
		parsetreefeatures = new ParseTreeBasedFeatures();
		process = new PreprocessText();
	}

}

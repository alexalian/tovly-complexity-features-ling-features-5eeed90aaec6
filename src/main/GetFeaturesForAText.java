/**
 * 
 */
package src.main;

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
 * @author svajjala Demonstration to get whatever features, for a given text
 *         file. Prints the output as a series of Feature, Value strings.
 */
public class GetFeaturesForAText {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String inputFilePath = "/Users/svajjala/trial.txt";
		PreprocessText process = new PreprocessText();
		String fileContent = process.getFileContent(inputFilePath);
		List<?> taggedParsedSentences = process.preProcessFile(fileContent);
		@SuppressWarnings("unchecked")
		List<List<TaggedWord>> taggedSentences = (List<List<TaggedWord>>) taggedParsedSentences.get(0);
		@SuppressWarnings("unchecked")
		List<Tree> parsedSentences = (List<Tree>) taggedParsedSentences.get(1);
		@SuppressWarnings("unchecked")
		ArrayList<String> tokenizedSentences = (ArrayList<String>) taggedParsedSentences.get(2);
		TreeMap<String, Double> allFeatures = new TreeMap<String, Double>();

		TraditionalFeatures trad = new TraditionalFeatures();
		allFeatures.putAll(trad.getTraditionalFeatures(fileContent));

		POSTagBasedFeatures pos = new POSTagBasedFeatures();
		allFeatures.putAll(pos.getPOSTagBasedFeatures(taggedSentences));

		WordBasedFeatures word = new WordBasedFeatures();
		allFeatures.putAll(word.getWordBasedFeatures(tokenizedSentences));

		WordNetBasedFeatures wn = new WordNetBasedFeatures();
		allFeatures.putAll(wn.getWNFeatures(taggedSentences));

		PsycholingFeatures psych = new PsycholingFeatures();
		allFeatures.putAll(psych.getPsycholingFeatures(taggedSentences));

		ParseTreeBasedFeatures parse = new ParseTreeBasedFeatures();
		allFeatures.putAll(parse.getSyntacticComplexityFeatures(parsedSentences));

		for (String s : allFeatures.keySet()) // Prints all the features along with names.
		{
			System.out.println(s + ":" + allFeatures.get(s));
		}

	}

}

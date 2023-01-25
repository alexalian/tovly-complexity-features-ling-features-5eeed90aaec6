/**
 * 
 */
package src.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.IntStream;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.ScoredObject;
import src.features.POSTagBasedFeatures;
import src.features.ParseTreeBasedFeatures;
import src.features.PsycholingFeatures;
import src.features.TraditionalFeatures;
import src.features.WordBasedFeatures;
import src.features.WordNetBasedFeatures;
import src.features.WordlistsBasedFeatures;
import src.features.KBestParseBasedFeatures;
import src.preprocessing.PreprocessText;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;
import java.util.concurrent.atomic.*;

/**
 * @author sowmya
 *
 */
public class GetFeaturesForADir {

	/**
	 * @param args Purpose: Take a directory path, calculate features for all files
	 *             and save as a csv file.
	 */
	public static void main(String[] args) throws Exception {

		// args[0] is the directory path containing .txt files
		// args[1] is the .csv file to store the output.
		extractFeaturesForDir("Newsela__None.json", "sample10Newsela.csv"); // Change these two paths
		// to input dir, output
		// csv resp,
		// Compile the file, and
		// run it.
	}

	private static void extractFeaturesForDir(String jsonpath, String outputfilepath) throws Exception {
		// File jsonfile = new File(jsonpath);
		// parsing file "JSONExample.json"
		Object obj = new JSONParser().parse(new FileReader(jsonpath));

		// typecasting obj to JSONObject
		JSONArray jsonArray = (JSONArray) obj;

		// long startTime = System.nanoTime();
		AtomicInteger numDocs = new AtomicInteger(0);
		AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputfilepath));

		POSTagBasedFeatures pos = new POSTagBasedFeatures();
		WordBasedFeatures word = new WordBasedFeatures();
		ParseTreeBasedFeatures parse = new ParseTreeBasedFeatures();
		WordlistsBasedFeatures lists = new WordlistsBasedFeatures();
		TraditionalFeatures tradfeatures = new TraditionalFeatures();
		WordNetBasedFeatures wnfeatures = new WordNetBasedFeatures();
		PsycholingFeatures psycholingfeatures = new PsycholingFeatures();
		KBestParseBasedFeatures kBestParseFeatures = new KBestParseBasedFeatures();

		PreprocessText preprocess = new PreprocessText();
		// long endTime = System.nanoTime();
		// for (int i = 0; i < jsonArray.size(); i++) {
		IntStream stream = IntStream.range(1, jsonArray.size());
		processFile(0, jsonArray, numDocs, startTime, bw, pos, word, parse, lists, tradfeatures, wnfeatures,
				psycholingfeatures, preprocess, kBestParseFeatures);
		stream.parallel().forEach((i) -> {
			try {
				processFile(i, jsonArray, numDocs, startTime, bw, pos, word, parse, lists, tradfeatures, wnfeatures,
						psycholingfeatures, preprocess, kBestParseFeatures);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		bw.close();
		System.out.println("Finished writing all files!");
	}

	private static void processFile(int i, JSONArray jsonArray, AtomicInteger numDocs, AtomicLong startTime,
			BufferedWriter bw, POSTagBasedFeatures pos, WordBasedFeatures word, ParseTreeBasedFeatures parse,
			WordlistsBasedFeatures lists, TraditionalFeatures tradfeatures, WordNetBasedFeatures wnfeatures,
			PsycholingFeatures psycholingfeatures, PreprocessText preprocess, KBestParseBasedFeatures kBestParse)
			throws Exception {
		String header = "filename,";

		JSONObject jo = (JSONObject) jsonArray.get(i);
		int docsProcessed = numDocs.getAndIncrement();
		if (docsProcessed != 0 && docsProcessed % 10 == 0) {
			double timeDiff = ((System.currentTimeMillis() - startTime.get()) / 1000);
			System.out.println("Rate: " + (double) docsProcessed / timeDiff);
		}
		// if (i % 10 == 0) {
		// System.out.println("Printing for file: " + jo.get("filepath"));
		// }
		String content = (String) jo.get("text");
		String filepath = (String) jo.get("filepath");
		List<?> taggedParsedSentences = preprocess.preProcessFile(content);
		@SuppressWarnings("unchecked")
		List<List<TaggedWord>> taggedSentences = (List<List<TaggedWord>>) taggedParsedSentences.get(0);
		@SuppressWarnings("unchecked")
		List<Tree> parsedSentences = (List<Tree>) taggedParsedSentences.get(1);
		@SuppressWarnings("unchecked")
		ArrayList<String> tokenizedSentences = (ArrayList<String>) taggedParsedSentences.get(2);
		@SuppressWarnings("unchecked")
		List<List<ScoredObject<Tree>>> kBestParsedSentences = (List<List<ScoredObject<Tree>>>) taggedParsedSentences.get(3);

		TreeMap<String, Double> allFeatures = new TreeMap<String, Double>();
		allFeatures.putAll(pos.getPOSTagBasedFeatures(taggedSentences));
		allFeatures.putAll(word.getWordBasedFeatures(tokenizedSentences));
		allFeatures.putAll(tradfeatures.getTraditionalFeatures(content));
		allFeatures.putAll(wnfeatures.getWNFeatures(taggedSentences));
		allFeatures.putAll(psycholingfeatures.getPsycholingFeatures(taggedSentences));
		allFeatures.putAll(parse.getSyntacticComplexityFeatures(parsedSentences));
		allFeatures.putAll(kBestParse.getSyntacticComplexityFeatures(kBestParsedSentences));

		String temp = filepath;
		for (String s : allFeatures.keySet()) // Prints all the features along with names.
		{
			if (i == 0) {
				header += "," + s;
			}
			temp += "," + allFeatures.get(s);
		}
		if (i == 0) {
			bw.write(header);
			bw.newLine();
		}
		bw.write(temp);
		bw.newLine();
	}

}
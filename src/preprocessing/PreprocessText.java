/**
 * 
 */
package src.preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParserQuery;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.ScoredObject;

/**
 * @author svajjala
 *
 */
public class PreprocessText {

	/**
	 * @param args
	 */
	private static MaxentTagger tagger;// = new MaxentTagger("models/english-left3words-distsim.tagger");
	private static LexicalizedParser lp;// = LexicalizedParser.loadModel("models/englishPCFG.ser.gz");
	private static DocumentPreprocessor tokenizer;

	public static void main(String[] args) throws Exception {

		// EXAMPLE USAGE.
		PreprocessText preprocess = new PreprocessText();
		String content = preprocess.getFileContent("/Users/sowmya/dummy.txt");
		System.out.println(content);

		List<?> taggedParsedSentences = preprocess.preProcessFile(content);
		@SuppressWarnings("unchecked")
		List<List<TaggedWord>> taggedSentences = (List<List<TaggedWord>>) taggedParsedSentences.get(0);
		// @SuppressWarnings("unchecked") List<Tree> parsedSentences = (List<Tree>)
		// taggedParsedSentences.get(1);
		@SuppressWarnings("unchecked")
		List<String> tokenizedSentences = (List<String>) taggedParsedSentences.get(2);

		for (String sentence : tokenizedSentences) {
			System.out.println(sentence);
			System.out.println(taggedSentences.get(1).toString());
		}
		// System.out.println(taggedSentences.get(0).toString());
	}

	public PreprocessText() {
		tagger = new MaxentTagger("models/english-left3words-distsim.tagger");
		lp = LexicalizedParser.loadModel("models/englishPCFG.ser.gz");
	}

	/**
	 * Reads a file and returns its textual content.
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public String getFileContent(String filePath) throws Exception {
		String content = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
			String dummy;
			while ((dummy = br.readLine()) != null) {
				content += dummy + " ";
			}
			br.close();
		} catch (Exception ex) {
			System.out.println("Error while reading the file: " + filePath + " " + ex.toString());
		}
		return content;
	}

	/**
	 * Performs sentence splitting, tokenizing, tagging and parsing.
	 * 
	 * @param content : textual content, as string
	 * @return A list object with two objects: Tagged sentences and Parsed
	 *         sentences.
	 * @throws Exception
	 */
	public List<?> preProcessFile(String content) throws Exception {
		List<Object> finalList = new ArrayList<Object>();

		try {
			tokenizer = new DocumentPreprocessor(new StringReader(content));

			List<List<TaggedWord>> taggedSentences = new ArrayList<List<TaggedWord>>();
			List<Tree> parsedSentences = new ArrayList<Tree>();
			List<List<ScoredObject<Tree>>> kParsedSentences = new ArrayList<List<ScoredObject<Tree>>>();
			List<String> tokenizedSentences = new ArrayList<String>();

			for (List<HasWord> sentence : tokenizer) {
				taggedSentences.add(tagger.tagSentence(sentence));
				parsedSentences.add(lp.apply(sentence));
				LexicalizedParserQuery lpq = lp.lexicalizedParserQuery();
				lpq.parse(sentence);
				kParsedSentences.add(lpq.getKBestPCFGParses(10));
				parsedSentences.add(lp.apply(sentence));
				tokenizedSentences.add(listToString(sentence));
			}
			finalList.add(taggedSentences);
			finalList.add(parsedSentences);
			finalList.add(tokenizedSentences);
			finalList.add(kParsedSentences);
		} catch (Exception ex) {
			System.out.println("Error in preProcessFile() " + ex.toString());
		}
		return finalList;
	}

	public static String listToString(List<HasWord> sentence) throws Exception {
		String result = "";
		for (HasWord word : sentence) {
			result += word.word() + " ";
		}
		return result;
	}

}

/**
 * 
 */
package src.features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Extracts features based on specific wordlists (plan: add Dale-Chall, Academic
 * Wordlist, GSL list, LFP, subtlex, and Brooke's list) Right now, I am adding
 * only SubtlexUS into the features list. Expects a tokenized list of strings as
 * input.
 * 
 * @author svajjala
 */
public class WordlistsBasedFeatures {

	private static Map<String, Double> subtlexUS = new TreeMap<String, Double>();

	public WordlistsBasedFeatures() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public TreeMap<String, Double> getWordListsBasedFeatures(ArrayList<String> tokenizedSentences) throws Exception {
		TreeMap<String, Double> wordlistBasedFeatures = new TreeMap<String, Double>();
		int subtlexTokens = 0; // Number of words in the text that are in Subtlex
		double subtlexAvgFreq = 0; // Average subtlex frequency of the words in the text. Stores sum until the end
		for (String sentence : tokenizedSentences) {

			String[] wordsInSentence = sentence.split(" ");
			for (String word : wordsInSentence) {
				if (subtlexUS.containsKey(word)) {
					subtlexTokens++;
					subtlexAvgFreq += subtlexUS.get(word);
				}
			}
		}

		wordlistBasedFeatures.put("WordListFeatures_numTokensInSubtlexUS", (double) subtlexTokens);
		wordlistBasedFeatures.put("WordListFeatures_avgSubtlexUSFreqOfTokens", (double) subtlexAvgFreq);
		return wordlistBasedFeatures;
	}

	/**
	 * Loads the new Dale-Chall list from file. Saves words in an ArrayList
	 * 
	 * @throws Exception
	 * @return List of words.
	 */
	private static ArrayList<String> loadDaleChallList() throws Exception {
		ArrayList<String> daleChallList = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader("resources/newdalechall-3000.txt"));
		String dummy = "";
		while ((dummy = br.readLine()) != null) {
			daleChallList.add(dummy);
		}
		br.close();
		System.out.println("Loaded DaleChall list of " + daleChallList.size() + " words.");
		return daleChallList;
	}

	/**
	 * Loads the SUBTLexUS list from file. Saves words in subtlexUSWords ArrayList;
	 * words-logfrequencies in subtlexUS list.
	 * 
	 * @throws Exception
	 */
	private static void loadSubtlexUSList() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("resources/SUBTLexUS.txt"));
		String dummy = "";
		br.readLine();
		while ((dummy = br.readLine()) != null) {
			String[] temp = dummy.split(";");
			if (Double.parseDouble(temp[5]) > 1.0) {
				subtlexUS.put(temp[0], Double.parseDouble(temp[5])); // Store the SubtlexLog10WF. [5] is the actual freq.
				// subtlexUSWords.add(temp[0]);
			}
		}
		br.close();
	}

	/**
	 * Load Julian Brooke's ranked list of complex words. Consider all versions of a
	 * word (hope or hope/O or hope/V or hope[2] or whatever as one word, hope)
	 * Right now, I am not sure what to make out of this.
	 * 
	 * @param BrookeListFilePath (/Users/svajjala/Downloads/JulianBrookeWordList.txt)
	 * @return
	 * @throws Exception
	 */
	private static ArrayList<String> loadBrookesList() throws Exception {
		ArrayList<String> brookesList = new ArrayList<String>();
		String dummy = "";
		BufferedReader br = new BufferedReader(new FileReader("resources/JulianBrookeWordList.txt"));
		while ((dummy = br.readLine()) != null) {
			String temp;
			if (dummy.contains("/")) {
				temp = dummy.split("/")[0];
			} else if (dummy.contains("[")) {
				temp = dummy.split("\\[")[0];
			} else {
				temp = dummy;
			}
			if (!brookesList.contains(temp)) {
				brookesList.add(temp);
			}
		}
		br.close();
		System.out.println("Loaded Brooke's word list with: " + brookesList.size() + "  words");
		return brookesList;
	}

	/**
	 * Loads the AcademicWord list from file. Has ~3000 words. I am not using the
	 * collapsed version with 570 families as of now. Saves words in an ArrayList
	 * 
	 * @throws Exception
	 * @return List of words.
	 */
	private static ArrayList<String> loadAWL() throws Exception {
		ArrayList<String> awl = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader("resources/awl-final.txt"));
		String dummy = "";
		while ((dummy = br.readLine()) != null) {
			awl.add(dummy);
		}
		br.close();
		System.out.println("Loaded Academic wordlist of " + awl.size() + " words.");
		return awl;
	}
}

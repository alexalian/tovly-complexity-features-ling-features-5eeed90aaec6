/*
Phantom Readability Library for Java
Copyright (C) 2009 Niels Ott

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package src.features.trad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spiaotools.SentParDetector;

/**
 * This class produces a text analysis suitable for computing
 * {@link de.drni.readability.phantom.Readability}. The most simple usage is to
 * specify the entire input text in a single string. However, the class can also
 * handle text that already has been tokenized by an some given external magic.
 * Various other constellations of given analyses can be used.
 * <p>
 * The internal sentence counter and tokenizer are based on relatively simple
 * RegEx magic. Hence using external analysis components with higher accuracy
 * may improve the accuracy of the analysis results to a certain extend.
 * <p>
 * Some bits and pieces of this class are inspired by the Java Fathom library by
 * Larry Ogrodnek, a port of Perl's Lingua::EN:Fathom.
 * 
 * @author Niels Ott
 * @version $Id: TextAnalyzer.java 324 2009-07-14 13:37:55Z
 *          nott@SFS.UNI-TUEBINGEN.DE $
 */
public class TextAnalyzer {

	/**
	 * Regular expression matching characters of a word.
	 */
	public static final String WORD = "([\\p{L}][-'\\p{L}]*)";

	private static String BEGIN_QUOT = "\"'»«‹›‚„‘“";
	private static String END_QUOT = "\"'»«‹›’”";
	private static ArrayList<String> uniqueWords;

	public static int charCount4LongestWord = 0;
	public static int syllCount4LongestWord = 0;
	public static int senCount4LongestSen = 0;

	/**
	 * Regular expression matching the end of a sentence.
	 */
	public static final String SENTENCE_END = "(" + "(\\p{L}([\\.\\!\\?:;…]|\\.\\.\\.)[" + END_QUOT + "]?\\s)|" + // sentence
																																																								// ending
																																																								// with
																																																								// optional
																																																								// closing
																																																								// quotemark
			"(\\p{L}[,;][" + END_QUOT + "]\\s)|" + // in-sentence direct speech or quotation ending
			"(\\p{L},\\s[" + BEGIN_QUOT + "]((…|\\.\\.\\.)\\s?)?\\p{L})|" + // in-sentence direct speech or quotation start
			"([\\p{L}\\p{N}]\\)[\\.\\!\\?:;]\\s)" + ")";

	/**
	 * The default abbreviation list, this is used by the default constructor.
	 */
	public static final String[] DEFAULT_ABBREVIATIONS = { "Adm", "Apr", "Aug", "Brig", "ca", "Capt", "cf", "Cmdr", "Co",
			"Col", "Comdr", "Corp", "Dec", "Det", "Dr", "Esq", "etc", "Feb", "fig", "Gen", "Gov", "ie", "Inc", "Insp", "Jan",
			"Jr", "jul", "Jun", "Lt", "ltd", "Ltd", "M", "Maj", "Mar", "Messrs", "Mlle", "Mme", "Mmes", "Mr", "Mrs", "Ms",
			"no", "Nov", "Oct", "PLC", "Prof", "Pty", "Rep", "Rev", "Sen", "Sep", "Sept", "Sgt", "Snr", "Sr", "St", "vs" };

	private static final String WORD_PLUS_BOUNDARIES = "\\b" + WORD + "\\b";

	/**
	 * Constructs a new analyzer with a custom abbreviations list.
	 * 
	 * @param abbreviationList a list of abbreviations without the trailing dots.
	 */
	public TextAnalyzer(List<String> abbreviationList) {
	}

	/**
	 * Constructs a new analyzer with the default abbreviations list.
	 */
	public TextAnalyzer() {
		this(Arrays.asList(DEFAULT_ABBREVIATIONS));
	}

	/**
	 * Does simple sledgehammer-tokenization based on RegExes.
	 * 
	 * @param text the tex to tokenize.
	 * @return The list of words (excluding possible delimiters).
	 */
	private List<String> simpleTokenizer(String text) {
		// add word boundaries around the text:
		String t = " " + text + " ";
		Matcher m = Pattern.compile(WORD_PLUS_BOUNDARIES).matcher(t);
		// collect grouped matches in a list:
		LinkedList<String> words = new LinkedList<String>();
		while (m.find()) {
			words.add(m.group(1));
		}
		return words;
	}

	private static List<String> simpleTokenizerasAbove(String text) {
		// add word boundaries around the text:
		String t = " " + text + " ";
		Matcher m = Pattern.compile(WORD_PLUS_BOUNDARIES).matcher(t);
		// collect grouped matches in a list:
		LinkedList<String> words = new LinkedList<String>();
		while (m.find()) {
			words.add(m.group(1));
		}
		return words;
	}

	// Using SentPar
	public static int splitSentences(String input) {
		SentParDetector sp = new SentParDetector();
		String all = sp.markupRawText(2, input);

		String[] splitsen = all.split("\n+");

		for (String sen : splitsen) {
			int lenTemp = simpleTokenizerasAbove(sen).size();
			if (lenTemp > senCount4LongestSen) {
				senCount4LongestSen = lenTemp;
			}
		}

		return all.split("\n+").length;
	}

	/**
	 * Analyzes a given text. This method uses the default word tokenizer and the
	 * default sentence counter and the default syllable counter
	 * ({@link SyllableCounterPort}).
	 * 
	 * @param text the text to analyze.
	 * @return the text statistics used for computing readability.
	 */
	public TextStats analyze(String text) {

		return analyze(text, new SyllableCounterPort());

	}

	/**
	 * Analyzes a given text using the specified syllable counter. This method uses
	 * the default word tokenizer and the default sentence counter.
	 * 
	 * @param text       the text to analyze.
	 * @param sylCounter the syllable counter used for computing the number of
	 *                   syllables for each word.
	 * @return the text statistics used for computing readability.
	 */
	public TextStats analyze(String text, SyllableCounter sylCounter) {

		List<String> words = simpleTokenizer(text);
		int sentences = splitSentences(text);

		return analyze(words, sentences, sylCounter);
	}

	/**
	 * Analyzes the specified list of words and the specified number of sentences
	 * using the syllable counter {@link SyllableCounterPort}.
	 * 
	 * @param words        the words of the text to analyze.
	 * @param numSentences the number of sentences in the text.
	 * @return the text statistics used for computing readability.
	 */
	public TextStats analyze(Iterable<String> words, int numSentences) {

		return analyze(words, numSentences, new SyllableCounterPort());

	}

	/**
	 * Analyzes the specified list of words and the specified number of sentences
	 * using the specified syllable counter. Although this method is meant to be
	 * used for analyzing words only, any tokens can be fed into it. Non-word tokens
	 * such as punctuation will be safely ignored.
	 * 
	 * @param words        the words of the text to analyze.
	 * @param numSentences the number of sentences in the text.
	 * @return the text statistics used for computing readability.
	 * @param sylCounter the syllable counter used for computing the number of
	 *                   syllables for each word.
	 */
	public TextStats analyze(Iterable<String> words, int numSentences, SyllableCounter sylCounter) {

		Pattern wordPattern = Pattern.compile(WORD);

		// initialize results object
		StatsContainer stats = new StatsContainer();
		uniqueWords = new ArrayList<String>();

		stats.numSentences = numSentences;

		// loop over words
		for (String word : words) {

			// safety condition: only operate on things that are words:
			if (!wordPattern.matcher(word).matches()) {
				continue;
			}

			stats.numWords++;

			if (!uniqueWords.contains(word)) {
				uniqueWords.add(word);
			}

			int sylCount = sylCounter.countSyllables(word);

			if (sylCount > syllCount4LongestWord) {
				syllCount4LongestWord = sylCount;
			}

			int wordLen = word.length();

			if (wordLen > charCount4LongestWord) {
				charCount4LongestWord = wordLen;
			}

			stats.numSyllables += sylCount;
			stats.numCharacters += wordLen;

			// SMOG definition of polysyllabic words
			if (sylCount > 2) {
				stats.numPolySyllables++;
			}

			// some measure might need that
			if (sylCount == 1) {
				stats.numMonoSyllables++;
			}

			// long words as defined for LIX
			if (wordLen > 6) {
				stats.numLongWords++;
			}

		}

		// according to DuBay-04, the "hard words" in Gunning Fog
		// are the same as our polysyllables
		stats.numHardWords = stats.numPolySyllables;
		stats.numTypes = uniqueWords.size();
		stats.charCount4LongestWord = charCount4LongestWord;
		stats.senCount4LongestSen = senCount4LongestSen;
		stats.syllCount4LongestWord = syllCount4LongestWord;

		uniqueWords.clear();
		// System.err.println(stats);

		return stats;
	}

}

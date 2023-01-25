/*
Phantom Readability Library for Java
Copyright (C) 2009 Niels Ott

This very file is adapted from Java Fathom by Larry Ogrodnek which
has been released under the Perl license allowing for re-releasing it 
under the terms of the GPL.

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

import src.features.trad.TextAnalyzer;
import src.features.trad.TextStats;

/**
 * Entry class of the Phantom package for computing English text readability
 * measures.
 * 
 * Ported from Kim Ryan's Lingua::EN::Fathom by Larry Ogrodnek. Extended and
 * heavily modified by Niels Ott. This extended version includes even more
 * readability measures than the original version. Furthermore it does not use
 * the old Fathom classes for analysis any more.
 * <p>
 * The readability measures are computed on the basis of a {@link TextStats}
 * statistics. By default, the statistics are produced by {@link TextAnalyzer}.
 * Users can implement their own magic that produces {@link TextStats} and feed
 * these statistics into the readability formulas encapsulated by this class.
 * Furthermore, {@link TextAnalyzer} can take various amounts of given
 * linguistic analyses. Hence it might be a good idea to call the analyzer
 * separately and feet its output into {@link Readability}. There are two
 * different constructors for these purposes.
 * 
 *
 * @author Larry Ogrodnek
 * @author Niels Ott
 * @version $Id: Readability.java 325 2009-07-14 13:42:22Z
 *          nott@SFS.UNI-TUEBINGEN.DE $
 */
public class Readability {

	private TextStats stats;

	/**
	 * Constructs a new readability object by analyzing the given text.
	 * {@link TextAnalyzer} is used for computing the analysis on which the
	 * readability measures are based on.
	 * 
	 * @param text the text to analyze.
	 */
	public Readability(String text) {
		this(new TextAnalyzer().analyze(text));
	}

	/**
	 * Constructs a new readability object without doing analysis. The analysis is
	 * taken from the given statistics.
	 * 
	 * @param stats the given text statistics to compute the readability measures
	 *              from.
	 */
	public Readability(TextStats stats) {
		this.stats = stats;
	}

	public double calcFog() {
		return (wordsPerSentence() + percentHardWords()) * 0.4;
	}

	public double calcFlesch() {
		return 206.835 - (1.015 * wordsPerSentence()) - (84.6 * syllablesPerWords());
	}

	public double calcKincaid() {
		return (11.8 * syllablesPerWords()) + (0.39 * wordsPerSentence()) - 15.59;
	}

	public double calcColemanLiau() {

		return -29.5873 * ((double) stats.getNumSentences()) / stats.getNumWords()
				+ 5.8799 * ((double) stats.getNumCharacters()) / stats.getNumWords() - 15.8007;

	}

	public double calcARI() {

		return 4.71 * ((double) stats.getNumCharacters()) / stats.getNumWords() + 0.5 * wordsPerSentence() - 21.43;

	}

	public double calcSMOG() {

		return 1.0430 * Math.sqrt(30.0 * ((double) stats.getNumPolySyllables()) / stats.getNumSentences()) + 3.1291;

	}

	// "LIX är en akronym för läsbarhetsindex som är ett mått på hur avancerad en
	// text är. C.H. Björnsson introducerade metoden 1968."
	// attention: number of periods/meanings (?) is taken as number of sentences
	// here.
	public double calcLIX() {

		return wordsPerSentence() + (stats.getNumLongWords() * 100.0) / stats.getNumWords();

	}

	public double calcFORCAST() {

		// number of monosyllables per 150 words
		double N = 150 * ((double) stats.getNumMonoSyllables() / stats.getNumWords());

		return 20.0 - (N / 10.0);

	}

	/**
	 * Helper method computing
	 * <p>
	 * NumberOfWords / NumberOfSentences
	 * 
	 * @return words per sentence computed from the text statistics.
	 */
	private double wordsPerSentence() {
		return ((double) stats.getNumWords()) / stats.getNumSentences();
	}

	public double charactersPerWord() {
		return (double) (stats.getNumCharacters()) / stats.getNumWords();
	}

	/**
	 * Helper method computing
	 * <p>
	 * ( NumberOfHardWords / NumberOfWords ) * 100
	 * 
	 * @return percentage of hard words computed from the text statistics.
	 */
	private double percentHardWords() {
		return (((double) stats.getNumHardWords()) / stats.getNumWords()) * 100;
	}

	/**
	 * Helper method computing
	 * <p>
	 * ( NumberOfSyllables / NumberOfWords )
	 * 
	 * @return the overall ratio of syllables per word computed from the text
	 *         statistics.
	 */
	public double syllablesPerWords() {
		return ((double) stats.getNumSyllables()) / stats.getNumWords();
	}
}
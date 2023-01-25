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

/**
 * A simple container for text statistics. This class bears no intelligence at
 * all. The natural user of this class is the {@link TextAnalyzer}, however
 * other users may simply want to pass all contained values to the constructor.
 * 
 * 
 * @author Niels Ott
 * @version $Id: StatsContainer.java 324 2009-07-14 13:37:55Z
 *          nott@SFS.UNI-TUEBINGEN.DE $
 */
public class StatsContainer implements TextStats {

	protected int numWords = 0;
	protected int numSentences = 0;
	protected int numSyllables = 0;
	protected int numCharacters = 0;
	protected int numHardWords = 0;
	protected int numLongWords = 0;
	protected int numPolySyllables = 0;
	protected int numMonoSyllables = 0;
	protected int numTypes = 0;

	protected int charCount4LongestWord = 0;
	protected int syllCount4LongestWord = 0;
	protected int senCount4LongestSen = 0;

	/**
	 * Default constructor intializing all values to 0.
	 */
	public StatsContainer() {
	}

	/**
	 * Constructor that allows/requires to set each and every value in this
	 * container.
	 */
	public StatsContainer(int numCharacters, int numComplexWords, int numLongWords, int numPolySyllables,
			int numMonoSyllables, int numSyllables, int numSentences, int numWords, int numTypes, int longestSenLen,
			int longestCharLen, int longestSyllLen) {
		this.numCharacters = numCharacters;
		this.numHardWords = numComplexWords;
		this.numLongWords = numLongWords;
		this.numPolySyllables = numPolySyllables;
		this.numPolySyllables = numMonoSyllables;
		this.numSentences = numSentences;
		this.numSyllables = numSyllables;
		this.numWords = numWords;
		this.numTypes = numTypes;
		this.senCount4LongestSen = longestSenLen;
		this.charCount4LongestWord = longestCharLen;
		this.syllCount4LongestWord = longestSyllLen;
	}

	public int getNumPolySyllables() {
		return numPolySyllables;
	}

	public int getNumMonoSyllables() {
		return numPolySyllables;
	}

	public int getNumWords() {
		return numWords;
	}

	public int getNumSentences() {
		return numSentences;
	}

	public int getNumSyllables() {
		return numSyllables;
	}

	public int getNumCharacters() {
		return numCharacters;
	}

	public int getNumHardWords() {
		return numHardWords;
	}

	public int getNumLongWords() {
		return numLongWords;
	}

	public int getNumTypes() {
		return numTypes;
	}

	public int getLongestSenLen() {
		return senCount4LongestSen;
	}

	public int getLongestWordLen() {
		return charCount4LongestWord;
	}

	public int getLongestSyllLen() {
		return syllCount4LongestWord;
	}

	public String toString() {

		return "[" + "numCharacters=" + numCharacters + "," + "numWords=" + numWords + "," + "numSentences=" + numSentences
				+ "," + "numHardWords=" + numHardWords + "," + "numLongWords=" + numLongWords + "," + "numSyllables="
				+ numSyllables + "," + "numMonoSyllables=" + numMonoSyllables + "," + "numPolySyllables=" + numPolySyllables
				+ "," + "numTypes=" + numTypes + "Longest Sentence Length=" + senCount4LongestSen + "Longest Word Length="
				+ charCount4LongestWord + "Longest Syllable Count Per Word=" + syllCount4LongestWord + "]";
	}

}

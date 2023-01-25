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

import src.features.trad.Readability;

/**
 * An interface specifying a statistics for a text. The {@link Readability}
 * class computes its measures upon the basis of this statistics. Refer to
 * comments of the getters below to learn what the meaning of each field in is.
 * <p>
 * The {@link de.drni.readability.phantom.analysis.StatsContainer} provides a
 * implementation that should be sufficient as a default implementation for most
 * scenarios.
 * 
 * @author Niels Ott
 * @version $Id: TextStats.java 324 2009-07-14 13:37:55Z
 *          nott@SFS.UNI-TUEBINGEN.DE $
 */
public interface TextStats {

	/**
	 * @return this should return the same value as {@link #getNumPolySyllables()}
	 */
	public int getNumHardWords();

	/**
	 * @return The number of sentences contained in the text.
	 */
	public int getNumSentences();

	/**
	 * @return The number of words contained in the text.
	 */
	public int getNumWords();

	/**
	 * @return the number of syllables found in all words contained in the rext.
	 */
	public int getNumSyllables();

	/**
	 * @return the number of characters of all words (excluding punctuation) in the
	 *         text.
	 */
	public int getNumCharacters();

	/**
	 * 
	 * @return the number of words in the text containing three or more syllables.
	 */
	public int getNumPolySyllables();

	/**
	 * 
	 * @return the number of words in the text containing exactly one syllable.
	 */
	public int getNumMonoSyllables();

	/**
	 * @return the number of words in the text containing seven or more characters.
	 */
	public int getNumLongWords();

	public int getNumTypes();

	public int getLongestSenLen();

	public int getLongestWordLen();

	public int getLongestSyllLen();

}
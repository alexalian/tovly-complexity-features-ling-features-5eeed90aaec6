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
 * An interface allowing users of {@link TextAnalyzer} to use their own
 * implementation of a syllable counter.
 * 
 * @author Niels Ott
 * @version $Id: SyllableCounter.java 324 2009-07-14 13:37:55Z
 *          nott@SFS.UNI-TUEBINGEN.DE $
 */
public interface SyllableCounter {

	/**
	 * Counts the syllables in a word.
	 * 
	 * @param word the word to count the syllables in.
	 * @return the number of syllables in the word.
	 */
	public int countSyllables(String word);

}

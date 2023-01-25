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

import java.util.regex.Pattern;

/**
 * A Java port of Laura Kassner's SyllableCounter Perl module which is designed
 * for English.
 * <p>
 * The original Perl module should pop up on CPAN sooner or later. This class is
 * a very direct port, rather a strict translation than a reformulation. The
 * source code even features the original comments. The only modification is the
 * use of {@link String#toLowerCase()} and {@link String#trim()} as initial
 * preparations instead of some RegExes.
 * 
 * @author Laura Kassner
 * @author Niels Ott
 * @version $Id: SyllableCounterPort.java 324 2009-07-14 13:37:55Z
 *          nott@SFS.UNI-TUEBINGEN.DE $
 */
public class SyllableCounterPort implements SyllableCounter {

	public static final String VERSION = "0.01";

	private String[] SubSyl = { "every", // second e is mute (no matter where in the word it appears)
			"cial", // old
			"tia", // old
			"cius", // old
			"[tcxgl]ious", // old, but improved to fit more endings
			"[cg]eous", // gorgeous, curvaceous
			"eau", // beauty, bureau
			"oui", // bouillon
			"eye", // because y counts as a vowel for vowel grouping
			"ieu", // lieutenant, adieu
			"giu", // belgium!
			"ion", // old - 2147 vs 47... ;( this gets lion etc wrong
			"iou", // old
			"sia$", // old
			"[gq]ue?$", // morgue, Prague, antique, unique
			"qu[aeiouy]{2}", // queen, aqueous
			"^f[io]re[^aeiou]", // very common first compound words
			"^house.", // same here and for the next few lines
			"^home.", // this is sort of annoying, but helpful
			"^horse.", "^some.", "^life.", "^ice.", "^bare.", "^side.",
			// maybe one ought to add -tio$ if one really wants to use this on
			// Shakespearean text: Mercu-tio, Hora-tio, etc.
	};

	private String[] AddSyl = { "[^aeiouyq][aeiouy]ity$", // not two vowels, because that"s covered by 3 vowels
			"[^aeiouy]iety$", // same here
			"ia", // old - this is imprecise, but too special-casey!
			"riet", // old
			"[rf]ier$", // terrier, pacifier
			"ieth$", // eightieth, fortieth...
			"dien", // old
			"[^aeiouq]ue[nl]", // influence, congruent, cruel
			"iu", // old
			"io", // old
			"ii", // old
			"^ide[ao]", // getting annoyed by special cases...
			"poe[tm]", // poem, poet, poetry, poetic justice...
			"(^poly|pre)[aeiou]", // polyandrous, polyester, preamble,
			"^re([iou]|a[^ds])", // reinstantiate, reuse, but not reason, read
			"quiet", "tuit", // intuition, gratuity
			"[^aeiouy][aeouy]at(e|ion)$", // create, delineate, mediate...
			"^co[ei]", // coerce, coexist, coincidence
			".[^aeiouy]re$", // acre, cadre, etc., exclude pre-
			"[^aeiouy][aiouy]ing$", // not two vowels, which would be covered by 3 vowels
			"[aeiou]{3}", // agreeable, palaeographer, ...
			"[aeiou]y[aeiou]", // where y functions as a consonant
			"^mc", // Scottish names
			"[^q][aeiou]esc", // acquiesce
			"[^aeiouy][aeiouy]able", // arguable,
			"[^aeiouy][aeiouy]is[mt]$", // altruism, etc - but not maoism - covered by 3 vowels
			"[^aeiouyl]le$", // bottle, fickle, ...i/able
			"[^l]lien", // alien, salient [1]
			"scien", // science, ...scient (gets conscience wrong...)
			"^coa[dgx].", // [2] - removed the l, coal- always got wrong
			"[^gq]ua[^auieo]" // i think this fixes more than it breaks
			// (says the author of the other module)
	};

	private String[] edExceptions = { "bed", "biped",
			// "red", // this gets so many false occurrences!
			"sled", "bred", "[^aeiou]shed", "cursed", "beloved", "blessed", "crooked", "deuced", "learned", "peaked",
			"wicked", "wretched", "eed" };

	/**
	 * Private helper method that emulates Perl's string matching. Java's
	 * String.match() always matches for entire strings. Using the full feature
	 * RegEx engine in every occurence would be tedious.
	 * 
	 * @param s    the string to match (parts of).
	 * @param find the RegEx pattern.
	 * @return true if a part of the string matches, false otherwise.
	 */
	private boolean m(String s, String find) {
		Pattern p = Pattern.compile(find);
		return p.matcher(s).find();
	}

	public int countSyllables(String w) {

		// Modification by Niels: using Java's magic here instead of original RegExes
		String word = w.toLowerCase().trim();
		int syll = 0;

		// compounds that have a dash or are written apart:
		// calculate syllables for all parts separately
		// to deal with mute e at end of single parts
		String[] parts = word.split("[\\-\\s]+");

		for (String part : parts) {
			// 1) check for contractions and their addition to
			// syllable count

			// 1 a) genitive (or other) s after sibilants with optional mute e
			// add one to syllable count
			if (m(part, "((sh|ch|[xsz])e?|ge|ce)\\'s$")) {
				syll++;
			}
			// and chop off all the 's to access the rest of the word
			part = part.replaceFirst("\\'s$", "");

			// 1 b) n't after consonant and possible mute e
			// not: ain't, can't, won't etc.
			// adds one syllable
			if (m(part, "[^aeiouy]e?n\\'t$")) {
				syll++;
			}
			// chop off n't
			part = part.replaceFirst("n\\'t$", "");

			// and chop off all remaining contractions
			// because they don't add any more syllable count
			// ('d, 've, 'll, ' for plural genitive, 'm, 're)
			part = part.replaceFirst("\\'(d|ve|ll|m|re)$", "");
			// in cases where we have a contraction further inside a word
			// just remove the ' and hope for the best
			part = part.replaceAll("\\'", "");

			// 2) mini-morphological analysis
			// for plurals/3rd person, -ed, -ment, -ness, -ful(ly) etc.
			//
			// 2 a) plural or 3rd person -es after sibilants and l counts as a syllable
			if (m(part, "(sh|ch|[szgxc]|[^aeiouy]l)es$")) {
				syll++;
			}

			// get rid of all plural es - others don't add any syllable count
			// this includes es after another vowel
			//
			// simple plural s doesn't matter, so we leave it there
			// besides if we just chopped off the s we'd also modify those words
			// that end with simple s and are no plural,
			// which results in conflicts with the patterns
			// also exclude -ees
			if (!m(part, "ees$")) {
				part = part.replaceFirst("es$", "");
			}

			// 2 b) -ed after d, t and Cons-l is not muted, else it is
			if (m(part, "([dt]|[^aeiouyl]l)ed$")) {
				syll++;
			}

			// now chop off -ed unless the word is one of the exceptions
			// (there are too many to ignore them...)
			// treatment of -bed is too uniform, let's see what it does though
			boolean chopoff = true;
			for (String stopword : edExceptions) {
				if (m(part, stopword + "$")) {
					chopoff = false;
					break;
				}
			}
			if (chopoff) {
				part = part.replaceFirst("ed$", "");
			}

			// 2 c) ment, ness, ful, dom, ly, ways, wise, hood
			// those could be stacked, so do a while loop
			// e.g. blamelessness, blamelessly, shamefully
			// ..less is to avoid bless
			// ...ly is to avoid fly, ely, rely
			// ..age is to avoid age, sage etc
			while (m(part, "(.ments?|.ness|.ful|.doms?|.ways|.wise|.." + "less|.hoods?|.ships?|.some|...ly|..age)$")) {

				// add one syllable
				syll++;

				// chop it off
				part = part.replaceFirst("(ments?|ness|ful|doms?|ways|wise|" + "less|hoods?|ships?|some|ly|age)$", "");
			}

			// now we've removed some suffixes and can process the rest of the words as
			// in the old module, with more patterns to match to
			// except that we only chop off the -e after the pattern matching
			// because some of the patterns could not be matched otherwise
			// e.g. C-le, gue, etc. without e could be confused with nouns which end in gu,
			// C-l
			// after chopping off a suffix or a plural -es or an -ed, which already added
			// the
			// necessary syllable (it would get added twice that way)

			// special cases
			for (String s : SubSyl) {
				if (m(part, s)) {
					syll--;
				}
			}
			for (String s : AddSyl) {
				if (m(part, s)) {
					syll++;
				}
			}

			// chop off last e
			part = part.replaceFirst("e$", "");

			// split into vowel groups
			String scrugg[] = part.split("[^aeiouy]+");
			int scruggSize = scrugg.length;
			if (!((scrugg.length > 0) && (!scrugg[0].equals("")))) {
				scruggSize--;
			}

			// count vowel groupings
			syll += scruggSize;

			// this takes care of all-consonant words,
			// one-letter words, etc.
			// the other stuff that took care of them
			// is not needed at all!
			if (scruggSize == 0) {
				syll++;
			}

		}

		// at the very end:
		return syll;
	}

}

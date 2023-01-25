/**
 * 
 */
package src.features;

import java.util.TreeMap;

import src.features.trad.Readability;
import src.utils.genutils.NumUtils;

/**
 * Extracts traditional readability features.
 * 
 * @author svajjala Uses Phantom Library, written by Niels Ott and Laura
 *         Kassner. exist only in UIMA versions.
 */
public class TraditionalFeatures {

	public TraditionalFeatures() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// EXAMPLE USAGE:
		String text = "I could almost always tell when movies use fake dinosaurs .";
		TraditionalFeatures trad = new TraditionalFeatures();
		TreeMap<String, Double> eg = trad.getTraditionalFeatures(text);
		for (String s : eg.keySet()) // Prints all the features along with names.
		{
			System.out.println(s + "  " + eg.get(s));
		}
	}

	/**
	 * Extracts traditional readability features and formulae.
	 * 
	 * @param textContent - the tokenized text content as a string.
	 * @return TreeMap object containing various formulae names and their values.
	 * @throws Exception
	 */
	public TreeMap<String, Double> getTraditionalFeatures(String textContent) throws Exception {
		TreeMap<String, Double> tradFeatures = new TreeMap<String, Double>();
		Readability readability = new Readability(textContent);

		tradFeatures.put("TRAD_numChars", NumUtils.restrict2TwoDecimals(readability.charactersPerWord()));
		tradFeatures.put("TRAD_numSyll", NumUtils.restrict2TwoDecimals(readability.syllablesPerWords()));
		tradFeatures.put("TRAD_Kincaid", NumUtils.restrict2TwoDecimals(readability.calcKincaid()));
		tradFeatures.put("TRAD_Flesch", NumUtils.restrict2TwoDecimals(readability.calcFlesch()));
		tradFeatures.put("TRAD_ARI", NumUtils.restrict2TwoDecimals(readability.calcARI()));
		tradFeatures.put("TRAD_Coleman", NumUtils.restrict2TwoDecimals(readability.calcColemanLiau()));
		tradFeatures.put("TRAD_SMOG", NumUtils.restrict2TwoDecimals(readability.calcSMOG()));
		tradFeatures.put("TRAD_FOG", NumUtils.restrict2TwoDecimals(readability.calcFog()));
		tradFeatures.put("TRAD_FORCAST", NumUtils.restrict2TwoDecimals(readability.calcFORCAST()));
		tradFeatures.put("TRAD_LIX", NumUtils.restrict2TwoDecimals(readability.calcLIX()));
		return tradFeatures;
	}
}

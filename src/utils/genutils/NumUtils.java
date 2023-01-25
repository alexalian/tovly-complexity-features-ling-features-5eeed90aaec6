/**
 * 
 */
package src.utils.genutils;

/**
 * @author svajjala There are two methods that are repeatedly used in the code -
 *         one for restricting values to two decimals, one to handle division by
 *         zero. This small class takes care of these methods.
 */
public class NumUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/**
	 * Restricts a double value to two digits after decimal
	 * 
	 * @param D
	 * @return
	 */
	public static double restrict2TwoDecimals(double D) {
		int temp = (int) (D * 100);
		return (double) (temp / 100.0);
	}

	/**
	 * In cases where denominator (e.g., t-units do not exist in a small text)
	 * happens to be zero, this returns zero.
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	public static double handleDivByZero(double i1, double i2) {
		if (i2 == 0.0) {
			return 0.0;
		} else if (i1 / i2 > 10000) {
			return 0.0;
		} else {
			return restrict2TwoDecimals(i1 / i2);
		}
	}

}

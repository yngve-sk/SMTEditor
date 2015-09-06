package utils;

public class Math {

	/**
	 * 
	 * @param d
	 * @return
	 *  	the double with only two decimals
	 */
	public static double trim(double d) {
		int mul = (int)(d*100);
		double trim = 1.0*mul/100;;
		return trim;
	}
}

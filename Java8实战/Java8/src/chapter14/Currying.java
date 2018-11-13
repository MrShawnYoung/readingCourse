package chapter14;

import java.util.function.DoubleUnaryOperator;

/**
 * 科里化
 * 
 * @author Loops
 *
 */
public class Currying {

	public static void main(String[] args) {
		DoubleUnaryOperator convertCtoF = curriedConverter(9.0 / 5, 32);
		DoubleUnaryOperator convertUSDtoGBP = curriedConverter(0.6, 0);
		DoubleUnaryOperator convertKmtoMi = curriedConverter(0.6214, 0);

		System.out.println(convertCtoF.applyAsDouble(24));
		System.out.println(convertUSDtoGBP.applyAsDouble(100));
		System.out.println(convertKmtoMi.applyAsDouble(20));
	}

	public static double converter(double x, double y, double z) {
		return x * y + z;
	}

	public static DoubleUnaryOperator curriedConverter(double f, double b) {
		return (double x) -> x * f + b;
	}
}
package chapter11.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class Util {
	private static final Random RANDOM = new Random(0);
	private static final DecimalFormat formatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

	public static void delay() {
		try {
			// int delay = 1000;
			int delay = 500 + RANDOM.nextInt(2000);
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static double format(double number) {
		synchronized (formatter) {
			return new Double(formatter.format(number));
		}
	}
}
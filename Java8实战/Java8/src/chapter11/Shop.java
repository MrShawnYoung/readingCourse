package chapter11;

import java.util.Random;

import chapter11.utils.Discount;
import chapter11.utils.Discount.Code;
import chapter11.utils.Util;

public class Shop {
	private final String name;
	private final Random random;

	public Shop(String name) {
		this.name = name;
		random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
	}

	public String getPrice(String product) {
		double price = calculatePrice(product);
		Code code = Code.values()[random.nextInt(Code.values().length)];
		return name + ":" + price + ":" + code;
	}

	private double calculatePrice(String product) {
		Util.delay();
		return Util.format(random.nextDouble() * product.charAt(0) + product.charAt(1));
	}

	public String getName() {
		return name;
	}
}
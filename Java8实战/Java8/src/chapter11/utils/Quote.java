package chapter11.utils;

import chapter11.utils.Discount.Code;

/**
 * 解析操作封装
 * 
 * @author Loops
 *
 */
public class Quote {
	private final String shopName;
	private final double price;
	private final Code discountCode;

	public Quote(String shopName, double price, Discount.Code discountCode) {
		this.shopName = shopName;
		this.price = price;
		this.discountCode = discountCode;
	}

	public static Quote parse(String s) {
		String[] split = s.split(":");
		String shopName = split[0];
		double price = Double.parseDouble(split[1]);
		Code discountCode = Code.valueOf(split[2]);
		return new Quote(shopName, price, discountCode);
	}

	public String getShopName() {
		return shopName;
	}

	public double getPrice() {
		return price;
	}

	public Discount.Code getDiscountCode() {
		return discountCode;
	}
}
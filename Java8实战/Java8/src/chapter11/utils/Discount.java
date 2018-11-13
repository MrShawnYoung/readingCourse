package chapter11.utils;

/**
 * 折扣服务
 * 
 * @author Loops
 *
 */
public class Discount {
	/**
	 * 折扣代码
	 * 
	 * @author Loops
	 *
	 */
	public enum Code {
		NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);

		private final int percentage;

		Code(int percentage) {
			this.percentage = percentage;
		}
	}

	public static String applyDiscount(Quote quote) {
		/* 将折扣代码应用于商品最初的原始价格 */
		return quote.getShopName() + " price is " + Discount.apply(quote.getPrice(), quote.getDiscountCode());
	}

	private static double apply(double price, Code code) {
		/* 模拟Discount服务响应的延迟 */
		Util.delay();
		return Util.format(price * (100 - code.percentage) / 100);
	}
}
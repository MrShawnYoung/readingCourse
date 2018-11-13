package chapter11.async;

import java.util.concurrent.Future;

public class ShopMain {

	public static void main(String[] args) {
		Shop shop = new Shop("BestShop");
		long start = System.nanoTime();
		/* 查询商店，试图取得商品的价格 */
		Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
		long invocationTime = ((System.nanoTime() - start) / 1_000_000);
		System.out.println("Invocation returned after " + invocationTime + " msecs");
		// 执行更多任务，比如查询其他商店
		doSomethingElse();
		// 在计算商品价格的同时
		try {
			/* 从Future对象中读取价格，如果价格未知，会发生阻塞 */
			double price = futurePrice.get();
			System.out.printf("Price is %.2f%n", price);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
		System.out.println("Price returned after " + retrievalTime + " msecs");
	}

	private static void doSomethingElse() {
		System.out.println("Doing something else...");
	}
}
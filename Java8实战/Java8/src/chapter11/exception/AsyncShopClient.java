package chapter11.exception;

import java.util.concurrent.Future;

public class AsyncShopClient {

	public static void main(String[] args) {
		AsyncShop shop = new AsyncShop("BestShop");
		long start = System.nanoTime();
		// 2个方法效果等价
		// Future<Double> futurePrice = shop.getPrice("myPhone");
		Future<Double> futurePrice = shop.getPriceAsync("myPhone");
		long incocationTime = ((System.nanoTime() - start) / 1_000_000);
		System.out.println("Invocation returned after " + incocationTime + " msecs");
		try {
			System.out.println("Price is " + futurePrice.get());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		long retrivalTime = ((System.nanoTime() - start) / 1_000_000);
		System.out.println("Price returned after " + retrivalTime + " msecs");
	}
}
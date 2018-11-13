package chapter11.exception;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import chapter11.utils.Util;

/**
 * 错误处理
 * 
 * @author Loops
 *
 */
public class AsyncShop {
	private final String name;
	private final Random random;

	public AsyncShop(String name) {
		this.name = name;
		random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
	}

	public Future<Double> getPrice(String product) {
		CompletableFuture<Double> futurePrice = new CompletableFuture<>();
		new Thread(() -> {
			try {
				double price = calculatePrice(product);
				/* 如果价格计算正常结束，完成Future操作并设置商品价格 */
				futurePrice.complete(price);
			} catch (Exception ex) {
				/* 否则就抛出导致失败的异常，完成这次Future操作 */
				futurePrice.completeExceptionally(ex);
			}
		}).start();
		return futurePrice;
	}

	public Future<Double> getPriceAsync(String product) {
		return CompletableFuture.supplyAsync(() -> calculatePrice(product));
	}

	private double calculatePrice(String product) {
		Util.delay();
		if (true)
			throw new RuntimeException("product not available");
		return Util.format(random.nextDouble() * product.charAt(0) + product.charAt(1));
	}
}
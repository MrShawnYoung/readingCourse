package chapter11.async;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import chapter11.utils.Util;

/**
 * 将同步方法转换为异步方法
 * 
 * @author Loops
 *
 */
public class Shop {
	private final String name;
	private final Random random;

	public Shop(String name) {
		this.name = name;
		random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
	}

	public double getPrice(String product) {
		return calculatePrice(product);
	}

	private double calculatePrice(String product) {
		Util.delay();
		return random.nextDouble() * product.charAt(0) + product.charAt(1);
	}

	public Future<Double> getPriceAsync(String product) {
		CompletableFuture<Double> futurePrice = new CompletableFuture<>();
		/* 创建CompletableFuture对象，它会包含计算的结果 */
		new Thread(() -> {
			/* 在另一线程中以异步方式执行计算 */
			double price = calculatePrice(product);
			/* 需长时间计算的任务结束并得出结果时，设置Future的返回值 */
			futurePrice.complete(price);
		}).start();
		/* 无需等待还没结束的计算，直接返回Future对象 */
		return futurePrice;
	}

	public String getName() {
		return name;
	}
}
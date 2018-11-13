package chapter11.discount;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chapter11.Shop;
import chapter11.utils.Discount;
import chapter11.utils.Quote;

/**
 * 最佳价格查询器(折扣)
 * 
 * @author Loops
 *
 */
public class BestPriceFinder {
	private final List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"),
			new Shop("MyFavoriteShop"), new Shop("BuyItAll"), new Shop("ShopEasy"));

	/* 自定义线程池 */
	private final Executor executor = Executors.newFixedThreadPool(shops.size(), new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		}
	});

	public List<String> findPricesSequential(String product) {
		return shops.stream()
				/* 取得每个shop对象中商品的原始价格 */
				.map(shop -> shop.getPrice(product))
				/* 在Quote对象中对shop返回的字符串进行转换 */
				.map(Quote::parse)
				/* 联系Discount服务，为每个Quote申请折扣 */
				.map(Discount::applyDiscount).collect(Collectors.toList());
	}

	public List<String> findPricesParallel(String product) {
		return shops.parallelStream().map(shop -> shop.getPrice(product)).map(Quote::parse).map(Discount::applyDiscount)
				.collect(Collectors.toList());
	}

	public List<String> findPricesFuture(String product) {
		List<CompletableFuture<String>> priceFutures = shops.stream()
				/* 以异步方式取得 每个shop中指定产品的原始价格 */
				.map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor))
				/* Quote对象存在时，对其返回的值进行转换 */
				.map(future -> future.thenApply(Quote::parse))
				/* 使用另一个异步任务构造期望的Future，申请折扣 */
				.map(future -> future.thenCompose(
						quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)))
				.collect(Collectors.toList());

		return priceFutures.stream()
				/* 等待流中的所有Future执行完毕，并提取各自的返回值 */
				.map(CompletableFuture::join).collect(Collectors.toList());
	}

	/* 优化 */
	public Stream<CompletableFuture<String>> findPricesStream(String product) {
		return shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor))
				.map(future -> future.thenApply(Quote::parse)).map(future -> future.thenCompose(
						quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)));
	}

	public void printPricesStream(String product) {
		long start = System.nanoTime();
		CompletableFuture[] futures = findPricesStream(product)
				.map(f -> f.thenAccept(s -> System.out
						.println(s + " (done in " + ((System.nanoTime() - start) / 1_000_000) + " msecs)")))
				.toArray(size -> new CompletableFuture[size]);
		CompletableFuture.allOf(futures).join();
		System.out.println("All shops have now responded in " + ((System.nanoTime() - start) / 1_000_000) + " msecs");
	}
}
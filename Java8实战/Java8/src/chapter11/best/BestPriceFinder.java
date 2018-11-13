package chapter11.best;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import chapter11.async.Shop;
import chapter11.utils.ExchangeService;
import chapter11.utils.ExchangeService.Money;

/**
 * 最佳价格查询器
 * 
 * @author Loops
 *
 */
public class BestPriceFinder {
	private final List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"),
			new Shop("MyFavoriteShop"), new Shop("BuyItAll"), new Shop("ShopEasy"));

	private final Executor executor =
			/* 创建一个线程池，线程池中线程的数目为100和商店数目二者中较小的一个值 */
			Executors.newFixedThreadPool(Math.min(shops.size(), 100), new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					/* 使用守护线程——这种方式不会阻止程序的关停 */
					t.setDaemon(true);
					return t;
				}
			});

	public List<String> findPricesSequential(String product) {
		return shops.stream().map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
				.collect(Collectors.toList());
	}

	public List<String> findPricesParallel(String product) {
		return shops.parallelStream()
				.map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
				.collect(Collectors.toList());
	}

	public List<String> findPricesFuture(String product) {
		List<CompletableFuture<String>> priceFutures = shops.stream().map(shop ->
		/* 使用CompletableFuture以异步方式计算每种商品的价格 */
		CompletableFuture
				.supplyAsync(() -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product), executor)))
				.collect(Collectors.toList());

		return priceFutures.stream()/* 等待所有异步操作结束 */
				.map(CompletableFuture::join).collect(Collectors.toList());
	}

	/* 带汇率计算 */
	public List<String> findPricesInUSD(String product) {
		List<CompletableFuture<Double>> priceFutures = new ArrayList<>();
		for (Shop shop : shops) {
			CompletableFuture<Double> futurePriceInUSD =
					/* 创建第一个任务查询商店取得商品的价格 */
					CompletableFuture.supplyAsync(() -> shop.getPrice(product)).thenCombine(
							CompletableFuture.supplyAsync(
									/* 创建第二个独立任务，查询美元和欧元之间的转换汇率 */
									() -> ExchangeService.getRate(Money.EUR, Money.USD)),
							/* 通过乘法整合得到的商品价格和汇率 */
							(price, rate) -> price * rate);
			priceFutures.add(futurePriceInUSD);
		}

		List<String> prices = priceFutures.stream().map(CompletableFuture::join).map(price -> " price is " + price)
				.collect(Collectors.toList());
		return prices;
	}

	/* 利用Java7的方法合并两个Future对象 */
	public List<String> findPricesInUSDJava7(String product) {
		/* 创建一个ExecutorService将任务提交到线程池 */
		ExecutorService executor = Executors.newCachedThreadPool();
		List<Future<Double>> priceFutures = new ArrayList<>();
		for (Shop shop : shops) {
			final Future<Double> futureRate = executor.submit(new Callable<Double>() {
				public Double call() {
					/* 创建一个查询欧元到美元转换汇率的Future */
					return ExchangeService.getRate(Money.EUR, Money.USD);
				}
			});
			Future<Double> futurePriceInUSD = executor.submit(new Callable<Double>() {
				public Double call() {
					try {
						/* 在第二个Future中查询指定商店中特定商品的价格 */
						double priceInEUR = shop.getPrice(product);
						/* 在查找价格操作的同一个Future中，将价格和汇率做乘法计算出汇后价格 */
						return priceInEUR * futureRate.get();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				}
			});
			priceFutures.add(futurePriceInUSD);
		}
		List<String> prices = new ArrayList<>();
		for (Future<Double> priceFuture : priceFutures) {
			try {
				prices.add(" price is " + priceFuture.get());
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		return prices;
	}
}
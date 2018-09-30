package java8;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 增强的Future
 * 
 * @author 杨弢
 *
 */
public class CompletableFutureDemo {
	public static Integer calc(Integer para) {
		try {
			// 模拟一个长时间的执行
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		return para * para;
	}

	public static Integer calc2(Integer para) {
		return para / 0;
	}

	public static Integer calc3(Integer para) {
		return para / 2;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		/* 异步执行任务 */
		final CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> calc(50));
		System.out.println(future.get());
		/* 流式调用 */
		CompletableFuture<Void> fu = CompletableFuture.supplyAsync(() -> calc(50)).thenApply((i) -> Integer.toString(i))
				.thenApply((str) -> "\"" + str + "\"").thenAccept(System.out::println);
		fu.get();
		/* 异常处理 */
		CompletableFuture<Void> fu2 = CompletableFuture.supplyAsync(() -> calc2(50)).exceptionally(ex -> {
			System.out.println(ex.toString());
			return 0;
		}).thenApply((i) -> Integer.toString(i)).thenApply((str) -> "\"" + str + "\"").thenAccept(System.out::println);
		fu2.get();
		/* 组合多个CompletableFuture */
		CompletableFuture<Void> fu3 = CompletableFuture.supplyAsync(() -> calc3(50))
				.thenCompose((i) -> CompletableFuture.supplyAsync(() -> calc3(i))).thenApply((str) -> "\"" + str + "\"")
				.thenAccept(System.out::println);
		fu3.get();

		CompletableFuture<Integer> intFuture = CompletableFuture.supplyAsync(() -> calc3(50));
		CompletableFuture<Integer> intFuture2 = CompletableFuture.supplyAsync(() -> calc3(25));
		CompletableFuture<Void> fu4 = intFuture.thenCombine(intFuture2, (i, j) -> (i + j))
				.thenApply((str) -> "\"" + str + "\"").thenAccept(System.out::println);
		fu4.get();
	}
}
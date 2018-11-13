package chapter6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.*;

/**
 * 收集器接口
 * 
 * @author Loops
 *
 * @param <T>
 */
public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

	/* 创建集合操作的起始点 */
	@Override
	public Supplier<List<T>> supplier() {
		return ArrayList::new;
	}

	/* 累积遍历过的项目，原位修改累加器 */
	@Override
	public BiConsumer<List<T>, T> accumulator() {
		return List::add;
	}

	/* 恒等函数 */
	@Override
	public Function<List<T>, List<T>> finisher() {
		return Function.identity();
	}

	/* 修改第一个累加器，将其与第二个累加器的内容合并 */
	@Override
	public BinaryOperator<List<T>> combiner() {
		return (list1, list2) -> {
			list1.addAll(list2);
			// 返回修改后的第一个累加器
			return list1;
		};
	}

	/* 为收集器添加IDENTITY_FINISH和CONCURRENT标志 */
	@Override
	public Set<Collector.Characteristics> characteristics() {
		return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH, CONCURRENT));
	}
}
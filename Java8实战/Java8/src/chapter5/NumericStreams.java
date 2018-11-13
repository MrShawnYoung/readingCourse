package chapter5;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import chapter4.Dish;
import static chapter4.Dish.menu;

/**
 * 数值流
 * 
 * @author Loops
 *
 */
public class NumericStreams {

	public static void main(String[] args) {
		List<Integer> numbers = Arrays.asList(3, 4, 5, 1, 2);
		Arrays.stream(numbers.toArray()).forEach(System.out::println);
		int calories = menu.stream().mapToInt(Dish::getCalories).sum();
		System.out.println("Number of calories:" + calories);

		IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
		Stream<Integer> stream = intStream.boxed();

		OptionalInt maxCalories = menu.stream().mapToInt(Dish::getCalories).max();
		int max = maxCalories.orElse(1);

		IntStream evenNumbers = IntStream.rangeClosed(1, 100).filter(n -> n % 2 == 0);
		System.out.println(evenNumbers.count());

		Stream<int[]> pythagoreanTriples = IntStream.rangeClosed(1, 100).boxed()
				.flatMap(a -> IntStream.rangeClosed(a, 100).filter(b -> Math.sqrt(a * a + b * b) % 1 == 0).boxed()
						.map(b -> new int[] { a, b, (int) Math.sqrt(a * a + b * b) }));
		pythagoreanTriples.limit(5).forEach(t -> System.out.println(t[0] + ", " + t[1] + ", " + t[2]));

		IntStream.rangeClosed(1, 100).boxed().flatMap(a -> IntStream.rangeClosed(a, 100)
				.mapToObj(b -> new double[] { a, b, Math.sqrt(a * a + b * b) }).filter(t -> t[2] % 1 == 0));
	}
}
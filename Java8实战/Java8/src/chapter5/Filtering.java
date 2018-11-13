package chapter5;

import java.util.Arrays;
import java.util.List;

import chapter4.Dish;
import static chapter4.Dish.menu;

import static java.util.stream.Collectors.toList;

/**
 * 过滤
 * 
 * @author Loops
 *
 */
public class Filtering {

	public static void main(String[] args) {
		List<Dish> vegetarianMenu = menu.stream().filter(Dish::isVegetarian).collect(toList());

		vegetarianMenu.forEach(System.out::println);

		List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
		numbers.stream().filter(i -> i % 2 == 0).distinct().forEach(System.out::println);

		List<Dish> dishesLimit3 = menu.stream().filter(d -> d.getCalories() > 300).limit(3).collect(toList());

		dishesLimit3.forEach(System.out::println);

		List<Dish> dishesSkip2 = menu.stream().filter(d -> d.getCalories() > 300).skip(2).collect(toList());

		dishesSkip2.forEach(System.out::println);
	}
}
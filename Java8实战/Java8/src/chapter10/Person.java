package chapter10;

import java.util.Optional;

public class Person {
	/* 人可能有车，也可能没有车，因此将这个字段声明为Optional */
	private Optional<Car> car;

	public Optional<Car> getCar() {
		return car;
	}
}
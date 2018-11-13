package chapter10;

import java.util.Optional;

/**
 * 应用Optional的几种模式
 * 
 * @author Loops
 *
 */
public class OptionalMain {

	public String getCarInsuranceName(Optional<Person> person) {
		return person.flatMap(Person::getCar).flatMap(Car::getInsurance).map(Insurance::getName)
				/* 如果Optional的结果值为空，设置默认值 */
				.orElse("Unknown");
	}
}
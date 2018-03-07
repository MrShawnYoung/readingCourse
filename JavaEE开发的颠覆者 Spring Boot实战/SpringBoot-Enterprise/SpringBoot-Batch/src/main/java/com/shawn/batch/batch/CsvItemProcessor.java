package com.shawn.batch.batch;

import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import com.shawn.batch.domain.Person;

public class CsvItemProcessor extends ValidatingItemProcessor<Person> {

	@Override
	public Person process(Person item) throws ValidationException {
		/* 调用自定义校验器 */
		super.process(item);
		/* 数据处理 */
		if (item.getNation().equals("汉族")) {
			item.setNation("01");
		} else {
			item.setNation("02");
		}
		return item;
	}
}
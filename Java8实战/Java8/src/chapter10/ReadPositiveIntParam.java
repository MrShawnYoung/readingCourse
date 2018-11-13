package chapter10;

import java.util.Optional;
import java.util.Properties;

public class ReadPositiveIntParam {

	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty("a", "5");
		props.setProperty("b", "true");
		props.setProperty("c", "-3");

		System.out.println(readDurationImperative(props, "a"));
		System.out.println(readDurationImperative(props, "b"));
		System.out.println(readDurationImperative(props, "c"));
		System.out.println(readDurationImperative(props, "d"));

		System.out.println(readDurationWithOptional(props, "a"));
		System.out.println(readDurationWithOptional(props, "b"));
		System.out.println(readDurationWithOptional(props, "c"));
		System.out.println(readDurationWithOptional(props, "d"));
	}

	public static int readDurationImperative(Properties props, String name) {
		String value = props.getProperty(name);
		/* 确保名称对应的属性存在 */
		if (value != null) {
			try {
				/* 将String属性转换为数字类型 */
				int i = Integer.parseInt(value);
				/* 检查返回的数字是否为正数 */
				if (i > 0) {
					return i;
				}
			} catch (NumberFormatException nfe) {

			}
		}
		/* 如果前述的条件都不满足，返回0 */
		return 0;
	}

	public static int readDurationWithOptional(Properties props, String name) {
		return Optional.ofNullable(props.getProperty(name)).flatMap(s -> {
			try {
				return Optional.of(Integer.parseInt(s));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}).filter(i -> i > 0).orElse(0);
	}
}
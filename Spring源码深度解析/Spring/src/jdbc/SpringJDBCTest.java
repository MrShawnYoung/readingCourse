package jdbc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringJDBCTest {

	public static void main(String[] args) throws Exception {
		ApplicationContext act = new ClassPathXmlApplicationContext(
				"springJDBC.xml");
		UserService userService = (UserService) act.getBean("userService");
		User user = new User();
		user.setName("张三ccc");
		user.setAge(20);
		user.setSex("男");
		userService.save(user);
		/*
		 * List<User> person1 = userService.getUsers(); for (User person2 :
		 * person1) { System.out.println(person2.getId() + " " +
		 * person2.getName() + " " + person2.getAge() + " " + person2.getSex());
		 * }
		 */
	}
}
package mybatis;

import mybatis.dao.UserMapper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserServiceTest {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring.xml");
		UserMapper userService = (UserMapper) context.getBean("userMapper");
		System.out.println(userService.getUser(1));
	}
}
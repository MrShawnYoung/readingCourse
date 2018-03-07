package com.shawn.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.shawn.springboot.config.AuthorSettings;

@RestController
/* 开启自动配置 */
@SpringBootApplication
public class DemoApplication {
	/* 读取配置文件 */
	@Value("${book.author}")
	private String bookAuthor;
	@Value("${book.name}")
	private String bookName;

	@Autowired
	private AuthorSettings authorSettings;

	@RequestMapping("/")
	String index() {
		return "book name is:" + bookName + " and book author is:" + bookAuthor + " author name is "
				+ authorSettings.getName() + " and author age is " + authorSettings.getAge();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
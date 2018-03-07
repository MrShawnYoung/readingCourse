package com.shawn.ba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class BAApplication {
	@RequestMapping(value = "/search", produces = { MediaType.APPLICATION_JSON_VALUE })
	public Person search(String personName) {
		return new Person(personName, 26, "nanjing");
	}

	public static void main(String[] args) {
		SpringApplication.run(BAApplication.class, args);
	}
}
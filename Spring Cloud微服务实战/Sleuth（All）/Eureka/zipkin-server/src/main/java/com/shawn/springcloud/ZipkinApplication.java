package com.shawn.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;

/*开启Zipkin Server*/
//@EnableZipkinServer
/* 开启Zipkin Stream Server */
@EnableZipkinStreamServer
@SpringBootApplication
public class ZipkinApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ZipkinApplication.class, args);
	}
}
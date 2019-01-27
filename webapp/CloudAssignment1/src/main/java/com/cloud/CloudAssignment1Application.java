package com.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.cloud.controller", "com.cloud.service", "com.cloud.dao", "com.cloud.service.impl",
		"com.cloud.pojo" })
public class CloudAssignment1Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CloudAssignment1Application.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(CloudAssignment1Application.class, args);
	}

}

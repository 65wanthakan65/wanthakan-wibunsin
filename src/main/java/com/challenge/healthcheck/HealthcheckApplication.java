package com.challenge.healthcheck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class HealthcheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthcheckApplication.class, args);
	}
	
//	@Bean
//	public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption,
//			@Value("${application-version}") String appVersion) {
//		return new OpenAPI().addServersItem(new Server().description("test").url("url"))
//				.info(new Info()
//						.title("Healthcheck application API")
//						.version(appVersion)
//						.description(appDesciption)
//						.termsOfService(""));
//
//	}
	
}

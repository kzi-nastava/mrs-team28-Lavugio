package com.backend.lavugio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LavugioApplication {

	public static void main(String[] args) {
		SpringApplication.run(LavugioApplication.class, args);
	}
}
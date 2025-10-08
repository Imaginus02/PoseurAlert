package com.reynaud.poseuralert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.reynaud.poseuralert.model")
public class PoseuralertApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoseuralertApplication.class, args);
	}

}

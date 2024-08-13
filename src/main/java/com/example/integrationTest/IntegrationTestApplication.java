package com.example.integrationTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UWAGA: Musi być uruchomiony Docker Desktop i uruchomiona baza danych MsSQL loving_swartz
 */
@SpringBootApplication
public class IntegrationTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationTestApplication.class, args);
	}

}

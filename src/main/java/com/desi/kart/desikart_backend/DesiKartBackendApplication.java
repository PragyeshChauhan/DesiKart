package com.desi.kart.desikart_backend;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DesiKartBackendApplication {
	@Value("${spring.profiles.active}")
	private String activeProfile;

	public static void main(String[] args) {
		SpringApplication.run(DesiKartBackendApplication.class, args);
	}

	@PostConstruct
	public void init() {
		System.out.println("Active profile: " + activeProfile);
	}


}

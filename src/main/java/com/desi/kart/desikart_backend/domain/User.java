package com.desi.kart.desikart_backend.domain;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private boolean isVerified;
	
	private String password;
	
	private boolean isActive;
	
	private String deviceToken;
}

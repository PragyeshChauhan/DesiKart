package com.desi.kart.desikart_backend.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private boolean isVerified;
	
	private String password;
	
	private boolean isActive;
	
	private String deviceToken;
}

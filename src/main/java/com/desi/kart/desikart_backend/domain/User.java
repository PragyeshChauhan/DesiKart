package com.desi.kart.desikart_backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Name is required")
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "Email is required")
	@Column(nullable = false)
	private String email;

	@NotBlank(message = "Phone is required")
	@Column(nullable = false)
	private String phone;
	
	private boolean isVerified;

	@NotBlank(message = "Password is required")
	@Column(nullable = false)
	private String password;
	
	private boolean isActive;
	
	private String deviceToken;

	private String provider ;

	private boolean  resetPasswordAfterLogin ;

	private List<Long> roleId;
}

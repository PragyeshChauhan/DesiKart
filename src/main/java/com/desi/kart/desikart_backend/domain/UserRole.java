package com.desi.kart.desikart_backend.domain;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserRole {

	@Id
	private String id;
	
	private String userId;
	
	private String roleId;
}

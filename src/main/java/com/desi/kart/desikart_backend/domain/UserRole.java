package com.desi.kart.desikart_backend.domain;

import java.util.List;

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
	
	private List<String> roleId;
}

package com.desi.kart.desikart_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserDTO {

    private Long id;
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private boolean isVerified;
	
	private boolean isActive;

	private String password;

	private String deviceToken;

}

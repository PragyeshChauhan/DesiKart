package com.desi.kart.desikart_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String id;
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private boolean isVerified;
	
	private boolean isActive;
}

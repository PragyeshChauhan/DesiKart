package com.desi.kart.desikart_backend.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OtpVerification {
	
	private String id;
	private String userId;          
	private String otp;
	private LocalDateTime generatedAt;
	private Boolean isUsed;
	
	 public OtpVerification(String userId, String otp, LocalDateTime generatedAt, Boolean isUsed) {
	        this.userId = userId;
	        this.otp = otp;
	        this.generatedAt = generatedAt;
	        this.isUsed = isUsed;
	 }
}

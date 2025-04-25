package com.desi.kart.desikart_backend.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OtpVerification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	private String otp;
	private LocalDateTime generatedAt;
	private Boolean isUsed;
	
	 public OtpVerification(Long userId, String otp, LocalDateTime generatedAt, Boolean isUsed) {
	        this.userId = userId;
	        this.otp = otp;
	        this.generatedAt = generatedAt;
	        this.isUsed = isUsed;
	 }
}

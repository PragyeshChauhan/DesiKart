package com.desi.kart.desikart_backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.ZonedDateTime;

@Entity
@Data
public class LoginBo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private String username;
    private String password;
    private String refreshToken;
    private String sessionId;
    private String ip;
    private String operatingSystem;
    private String zoneId;
    private String location;
    private Long userId;
    private String device;
    private String provider;
    private ZonedDateTime loginStampDateTime;
}

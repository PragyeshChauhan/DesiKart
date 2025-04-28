package com.desi.kart.desikart_backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_resets")
@Data
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    // Constructors
    public PasswordReset() {
    }

    public PasswordReset(String email, String token, LocalDateTime expirationDate) {
        this.email = email;
        this.token = token;
        this.expirationDate = expirationDate;
    }
}
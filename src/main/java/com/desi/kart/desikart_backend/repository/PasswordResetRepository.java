package com.desi.kart.desikart_backend.repository;

import com.desi.kart.desikart_backend.domain.PasswordReset;
import org.springframework.data.repository.CrudRepository;

public interface PasswordResetRepository extends CrudRepository<PasswordReset, Long> {
    PasswordReset findByToken(String token);
}

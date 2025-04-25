package com.desi.kart.desikart_backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.desi.kart.desikart_backend.domain.OtpVerification;

public interface OtpRepository extends CrudRepository<OtpVerification, String>{

    Optional<OtpVerification> findByUserIdAndOtpAndIsUsedFalse(Long userId, String otp);
}

package com.desi.kart.desikart_backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.desi.kart.desikart_backend.domain.OtpVerification;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends CrudRepository<OtpVerification, Long>{

    Optional<OtpVerification> findByUserIdAndOtpAndIsUsedFalse(Long userId, String otp);
}

package com.desi.kart.desikart_backend.repository;

import com.desi.kart.desikart_backend.domain.BaseRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseRolesRepo extends JpaRepository<BaseRoles , Long> {

    Optional<BaseRoles> findByIsDefaultTrue();
}

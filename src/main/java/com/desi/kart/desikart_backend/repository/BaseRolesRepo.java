package com.desi.kart.desikart_backend.repository;

import com.desi.kart.desikart_backend.domain.BaseRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseRolesRepo extends JpaRepository<BaseRoles , Long> {

    Optional<BaseRoles> findByIsDefaultTrue();
}

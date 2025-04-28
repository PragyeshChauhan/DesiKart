package com.desi.kart.desikart_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desi.kart.desikart_backend.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

    Optional <User> findByEmail(String token);

    User findByPassword(String oldPassword);
}

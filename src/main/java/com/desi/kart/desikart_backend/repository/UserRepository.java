package com.desi.kart.desikart_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desi.kart.desikart_backend.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

    User findByEmail(String token);

    User findByPassword(String oldPassword);
}

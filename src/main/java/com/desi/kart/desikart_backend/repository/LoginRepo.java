package com.desi.kart.desikart_backend.repository;

import com.desi.kart.desikart_backend.domain.LoginBo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface LoginRepo extends JpaRepository<LoginBo,Long> , Serializable {


}

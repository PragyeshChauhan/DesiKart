package com.desi.kart.desikart_backend.service;

import com.desi.kart.desikart_backend.domain.LoginBo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LoginService {

    public LoginBo authentication(LoginBo loginBo, HttpServletResponse response);


    public LoginBo authenticationByOauth(String email, String zoneId, String device, String location, String provider, HttpServletResponse response);
}

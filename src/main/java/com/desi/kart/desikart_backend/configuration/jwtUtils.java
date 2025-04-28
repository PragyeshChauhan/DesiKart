package com.desi.kart.desikart_backend.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class jwtUtils {

    private final String jwtSecretkey ="ghvyuqediwhiudgcwhiu74688";

    private final long  jwtExpirationMs = 86400000;

    private Logger log = LoggerFactory.getLogger(getClass());

    public  String generateJwtToken(UserDetails userDetails){

        Set<String> roles = userDetails.
                           getAuthorities().stream().
                           map(GrantedAuthority :: getAuthority).collect(Collectors.toSet());
        return   Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date( new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512,jwtSecretkey)
                .compact();
    }

    public String getUserName( String token ){

        return Jwts
                .parser()
                .setSigningKey(jwtSecretkey)
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token){

        try {
            Jwts.parser().setSigningKey(jwtSecretkey).parseClaimsJws(token);
            return true;
        }catch(Exception e){
            log.error("Error Whiling Validate Token error :: {} ", e.getMessage());
            return false ;
        }
    }

}

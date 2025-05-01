package com.desi.kart.desikart_backend.jwt.service;

import com.desi.kart.desikart_backend.spring.config.CustomSpringUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for generating, validating, and parsing JWT tokens.
 */
@Component
public class JwtTokenService {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);
    private static final String ISSUER = "desikart";

    private final String jwtSecretKey;
    private final long jwtExpirationMs;

    public JwtTokenService(@Value("${jwt.secret}") String jwtSecretKey,
                           @Value("${jwt.expirationMs}") long jwtExpirationMs) {
        this.jwtSecretKey = jwtSecretKey;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails the user details to include in the token
     * @return the generated JWT token
     */
    public String generateJwtToken(CustomSpringUser userDetails) {
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .claim("userFullName", userDetails.getUser().getName())
                .claim("userId", userDetails.getUser().getId())
                .claim("email", userDetails.getUser().getEmail())
                .claim("userZone", userDetails.getZoneId())
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token
     * @return the username, or null if the token is invalid
     */
    public String getUserName(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSecretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            logger.error("Error parsing username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validates a JWT token.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Error while validating token: {}", e.getMessage());
            return false;
        }
    }
}
package com.desi.kart.desikart_backend.resource;

import com.desi.kart.desikart_backend.constants.SystemConstants;
import com.desi.kart.desikart_backend.domain.LoginBo;
import com.desi.kart.desikart_backend.domain.User;
import com.desi.kart.desikart_backend.repository.UserRepository;
import com.desi.kart.desikart_backend.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public LoginBo passwordLogin(@RequestBody LoginBo loginBo, HttpServletResponse response) {
        try {
            return loginService.authentication(loginBo, response);
        } catch (Exception e) {
            log.error("Password login failed for username: {}", loginBo.getUsername(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new LoginBo();
        }
    }

    @GetMapping("/oauth2/success")
    public LoginBo handleOAuth2Login(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @RequestParam(value = "zoneId", defaultValue = "UTC") String zoneId,
            @RequestParam(value = "device", defaultValue = "Unknown") String device,
            @RequestParam(value = "location", defaultValue = "Unknown") String location,
            HttpServletResponse response) {
        try {
            String name = oAuth2User.getAttribute("name");

            String provider = oAuth2User.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().contains("google")) ? "google" : "github";
            String email = extractEmail(oAuth2User.getAttributes(),provider);
            if (email == null) {
                log.error("OAuth2 user email is null");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return new LoginBo();
            }
            return loginService.authenticationByOauth(email ,zoneId,device,location ,provider,response);
        }  catch (Exception e) {
            log.error("OAuth login failed for email: {}", oAuth2User.getAttribute("email"), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new LoginBo();
        }
    }


    private String extractEmail(Map<String, Object> attributes, String provider) {
        return switch (provider) {
            case "google" -> (String) attributes.get("email");
            case "github" -> {
                String email = (String) attributes.getOrDefault("email", null);
                if (email == null) {
                    log.warn("No email provided by GitHub, using login as fallback");
                    String login = (String) attributes.get("login");
                    email = login + "@github.com";
                }
                yield email;
            }
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

}
package com.desi.kart.desikart_backend.oauth.service;

import com.desi.kart.desikart_backend.spring.config.CustomSpringUser;
import com.desi.kart.desikart_backend.domain.BaseRoles;
import com.desi.kart.desikart_backend.domain.User;
import com.desi.kart.desikart_backend.notification.MailService;
import com.desi.kart.desikart_backend.repository.BaseRolesRepo;
import com.desi.kart.desikart_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BaseRolesRepo baseRolesRepo;

    @Autowired
    private MailService mailService;


    private static  final  Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // Delegate to default implementation to fetch user info from OAuth2 provider
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId(); // e.g., "google", "github"
        String email = extractEmail(attributes, provider);
        String name = attributes.getOrDefault("name", "Unknown").toString();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email, name, provider));
        CustomSpringUser springUser =  new CustomSpringUser(user);

        return new DefaultOAuth2User(
                springUser.getAuthorities(),
                attributes,
                "email"
        );
    }

    private String extractEmail(Map<String, Object> attributes, String provider) {

        return switch (provider) {
            case "google" -> attributes.get("email").toString();
            case "github" -> {

                yield attributes.getOrDefault("email", attributes.get("login") + "@github.com").toString();
            }
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    private User registerNewUser(String email, String name, String provider) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setProvider(provider);
        user.setPassword(passwordEncoder.encode("defaultPassword"));
        user.setActive(true);
        user.setResetPasswordAfterLogin(true);
        user.setVerified(true);

       Optional<BaseRoles> baseRoles = baseRolesRepo.findByIsDefaultTrue();
       if(baseRoles.isPresent()){
           user.setRoles(Set.of(baseRoles.get()));
       }
       try {
           mailService.sendWelcomeMail(email,name,provider);
       } catch (Exception e) {
         log.error("welcome mail hasn't send");
       }
     return userRepository.save(user);
    }
}

package com.desi.kart.desikart_backend.oauth.service;

import com.desi.kart.desikart_backend.domain.BaseRoles;
import com.desi.kart.desikart_backend.domain.User;
import com.desi.kart.desikart_backend.notification.MailService;
import com.desi.kart.desikart_backend.repository.BaseRolesRepo;
import com.desi.kart.desikart_backend.repository.UserRepository;
import com.desi.kart.desikart_backend.spring.config.CustomSpringUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BaseRolesRepo baseRolesRepo;

    @Autowired
    private MailService mailService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // e.g., "google", "github"
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = extractEmail(attributes, provider);
        String name = attributes.getOrDefault("name", "Unknown").toString();

        if (email == null) {
            log.error("No email provided by {} for user attributes: {}", provider, attributes);
            throw new IllegalArgumentException("Email is required for OAuth login");
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email, name, provider));

        CustomSpringUser springUser = new CustomSpringUser(user);

        return new DefaultOAuth2User(
                springUser.getAuthorities(),
                attributes,
                "email"
        );
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

    @Transactional
    public User registerNewUser(String email, String name, String provider) {
        log.info("Registering new user: email={}, provider={}", email, provider);

        User user = new User();
        user.setEmail(email);
        user.setUsername(email); // Use email as username for uniqueness
        user.setName(name);
        user.setProvider(provider);
        user.setPassword(null); // No password for OAuth users
        user.setActive(true);
        user.setResetPasswordAfterLogin(false); // Not applicable for OAuth
        user.setVerified(true);

        Optional<BaseRoles> baseRoles = baseRolesRepo.findByIsDefaultTrue();
        if (baseRoles.isPresent()) {
            user.setRoles(Set.of(baseRoles.get()));
        } else {
            log.warn("No default role found, assigning empty roles");
            user.setRoles(Set.of());
        }

        User savedUser = userRepository.save(user);

        try {
            mailService.sendWelcomeMail(email, name, provider);
            log.info("Welcome email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", email, e.getMessage());
        }

        return savedUser;
    }
}
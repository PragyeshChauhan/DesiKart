package com.desi.kart.desikart_backend.serviceimpl;

import com.desi.kart.desikart_backend.constants.SystemConstants;
import com.desi.kart.desikart_backend.domain.LoginBo;
import com.desi.kart.desikart_backend.domain.User;
import com.desi.kart.desikart_backend.jwt.service.JwtTokenService;
import com.desi.kart.desikart_backend.notification.MailService;
import com.desi.kart.desikart_backend.repository.LoginRepo;
import com.desi.kart.desikart_backend.repository.UserRepository;
import com.desi.kart.desikart_backend.service.LoginService;
import com.desi.kart.desikart_backend.spring.config.CustomSpringUser;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private MailService mailService;

    @Autowired
    private LoginRepo loginRepo;

    private final AuthenticationManager authenticationManager;

    public LoginServiceImpl( AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public LoginBo authentication(LoginBo loginBo, HttpServletResponse response) {
        if (loginBo == null || loginBo.getUsername() == null || loginBo.getPassword() == null) {
            log.error("Invalid login request: username or password is null");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new LoginBo();
        }

        try {
            User user = userRepository.findByUsername(loginBo.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + loginBo.getUsername()));

            if (user.getPassword() == null) {
                log.error("Password-based login not supported for user: {}", loginBo.getUsername());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return new LoginBo();
            }

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginBo.getUsername(), loginBo.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);

            return processLogin(user, loginBo, "password", response);
        } catch (IllegalArgumentException e) {
            log.error("Authentication failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new LoginBo();
        } catch (Exception e) {
            log.error("Authentication error for user: {}", loginBo.getUsername(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return new LoginBo();
        }
    }

    @Override
    public LoginBo authenticationByOauth(String email, String zoneId, String device, String location, String provider, HttpServletResponse response) {
        if (email == null) {
            log.error("OAuth email is null");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new LoginBo();
        }

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

            LoginBo loginBo = new LoginBo();
            loginBo.setUsername(user.getUsername());
            loginBo.setZoneId(zoneId != null ? zoneId : SystemConstants.defaultTimeZone);
            loginBo.setDevice(device != null ? device : "Unknown");
            loginBo.setLocation(location != null ? location : "Unknown");

            user.setProvider(provider);
            userRepository.save(user);

            return processLogin(user, loginBo, provider, response);
        } catch (IllegalArgumentException e) {
            log.error("OAuth authentication failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new LoginBo();
        } catch (Exception e) {
            log.error("OAuth authentication error for email: {}", email, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new LoginBo();
        }
    }

    private LoginBo processLogin(User user, LoginBo loginBo, String provider, HttpServletResponse response) {
        try {
            CustomSpringUser springUser = new CustomSpringUser(user);
            String jwtToken = jwtTokenService.generateJwtToken(springUser);
            loginBo.setToken(jwtToken);
            loginBo.setUserId(user.getId());
            loginBo.setProvider(provider);

            // Set login timestamp
            String zoneId = loginBo.getZoneId() != null ? loginBo.getZoneId() : SystemConstants.defaultTimeZone;
            ZonedDateTime loginTime = ZonedDateTime.now(ZoneId.of(zoneId));
            loginBo.setLoginStampDateTime(loginTime);

            // Save login
            loginRepo.save(loginBo);


            try {
                Map<String, String> context = Map.of(
                        "userName", loginBo.getUsername(),
                        "email", springUser.getUser().getEmail() != null ? springUser.getUser().getEmail() : "N/A",
                        "loginTime", loginTime.format(DateTimeFormatter.ofPattern(SystemConstants.defaultDateTimeFormatter)),
                        "location", loginBo.getLocation() != null ? loginBo.getLocation() : "Unknown",
                        "device", loginBo.getDevice() != null ? loginBo.getDevice() : "Unknown"
                );

                mailService.sendMail(
                        springUser.getUser().getEmail(),
                        context,
                        SystemConstants.LOGIN_NOTIFICATION_SUBJECT,
                        SystemConstants.LOGIN_ATTENTION_TEMPLATE
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return loginBo;
        } catch (Exception e) {
            log.error("Error processing login for user: {}", loginBo.getUsername(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new LoginBo();
        }
    }
}
package com.desi.kart.desikart_backend.serviceimpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import com.desi.kart.desikart_backend.domain.BaseRoles;
import com.desi.kart.desikart_backend.domain.PasswordReset;
import com.desi.kart.desikart_backend.notification.MailService;
import com.desi.kart.desikart_backend.repository.BaseRolesRepo;
import com.desi.kart.desikart_backend.repository.PasswordResetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.desi.kart.desikart_backend.domain.OtpVerification;
import com.desi.kart.desikart_backend.domain.User;
import com.desi.kart.desikart_backend.dto.UserDTO;
import com.desi.kart.desikart_backend.mapper.UserMapper;
import com.desi.kart.desikart_backend.notification.NotificationService;
import com.desi.kart.desikart_backend.repository.OtpRepository;
import com.desi.kart.desikart_backend.repository.UserRepository;
import com.desi.kart.desikart_backend.service.UserService;
import com.desi.kart.desikart_backend.utility.Utility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

@Service
public class UserServiceImpl implements UserService{

	private final EntityManager entityManager;

    @Autowired
	private  UserMapper userMapper;

	@Autowired
    private  UserRepository userRepository;

	private  PasswordResetRepository passwordResetRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private BaseRolesRepo baseRolesRepo;

	@Autowired
	private MailService mailService;

	@Autowired
    private NotificationService pushService;

	@Autowired
    private OtpRepository otpRepo;

	private Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    
    public UserServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

	@Override
	public UserDTO createUser(UserDTO userDTO) {
		if(userDTO==null) {
			return null;
		}
//		String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
//		userDTO.setPassword(encodedPassword);
		User user = userMapper.toDomain(userDTO);
		String otp = Utility.generateOtp();
	    OtpVerification otpVerification = new OtpVerification(user.getId(), otp, LocalDateTime.now(), false);
	    otpRepo.save(otpVerification);
        try {
            pushService.sendOtp(user.getDeviceToken(), otp);
        } catch (Exception e) {
			log.info("error whiling otp verification");
        }
        User use = userRepository.save(user);
		return userMapper.toDTO(use);
	}

	@Override
	public UserDTO updateUser(UserDTO userDTO) {
		if(userDTO==null) {
			return null;
		}
		User user = userMapper.toDomain(userDTO);
		user = userRepository.save(user);
		return userMapper.toDTO(user);
	}

	@Override
	public UserDTO getUser(String id) {
		Optional<User> user = userRepository.findById(id);
		if(user.isPresent()) {
			userMapper.toDTO(user.get());
		}else {
			return null;
		}
		return null;
	}

	@Override
	public UserDTO updateStatus(String id) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
	    CriteriaUpdate<User> update = criteriaBuilder.createCriteriaUpdate(User.class);
	    Root<User> root = update.from(User.class);
	    update.set("status", false).where(criteriaBuilder.equal(root.get("id"), id));
	    entityManager.createQuery(update).executeUpdate();
	    User updatedUser = entityManager.find(User.class, id);
		return userMapper.toDTO(updatedUser);
	}

	public void resetPassword(String token, String newPassword) {
		PasswordReset passwordReset = passwordResetRepository.findByToken(token);
		if (passwordReset == null) {
			throw new RuntimeException("Invalid or expired token.");
		}
		if (passwordReset.getExpirationDate().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Token has expired.");
		}
		Optional<User> user = userRepository.findByEmail(passwordReset.getEmail());
		if (user.isEmpty()) {
			throw new RuntimeException("User not found.");
		}
		user.get().setPassword(newPassword);  // Encode password securely
		userRepository.save(user.get());
		passwordResetRepository.delete(passwordReset);
	}

	@Override
	public void saveResetToken(String email,String token) {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isPresent()) {
			throw new RuntimeException("User not found with email: " + email);
		}
		LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(15);

		// Create PasswordReset entry
		PasswordReset passwordReset = new PasswordReset(user.get().getEmail(), token, expirationTime);
		passwordResetRepository.save(passwordReset);
		String resetLink = "https://desikart.com/reset-password?token=" + token;
		mailService.sendResetPasswordEmail(email, resetLink);
	}

	@Override
	public void userPasswordReset(String oldPassword, String newPassword) {
		if (oldPassword==null && newPassword==null){
			throw new RuntimeException("old password and new password must not be empty");
		}
		User user = userRepository.findByPassword(oldPassword);
		if(user==null){
			throw new RuntimeException("user not found");
		}
		user.setPassword(newPassword);
	}

	public User registerNewUser(String email, String name, String provider) {
		User user = new User();
		user.setEmail(email);
		user.setName(name);
		user.setProvider(provider);
		user.setPassword(passwordEncoder.encode("defaultPassword"));
		user.setActive(true);
		user.setResetPasswordAfterLogin(true);
		user.setVerified(true);

		Optional<BaseRoles> baseRoles = baseRolesRepo.findByIsDefaultTrue();
		baseRoles.ifPresent(roles -> user.setRoles(Set.of(roles)));
		try {
			mailService.sendWelcomeMail(email,name,provider);
		} catch (Exception e) {
			log.error("welcome mail hasn't send");
		}
		return userRepository.save(user);
	}
}

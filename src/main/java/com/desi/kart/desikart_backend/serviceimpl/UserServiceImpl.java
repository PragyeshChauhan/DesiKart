package com.desi.kart.desikart_backend.serviceimpl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
	
	private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private NotificationService pushService;
    private OtpRepository otpRepo;
    
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository,EntityManager entityManager,OtpRepository otpRepo,NotificationService pushService) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.otpRepo = otpRepo;
    }

	@Override
	public UserDTO createUser(UserDTO userDTO) {
		if(userDTO==null) {
			return null;
		}
		User user = userMapper.toDomain(userDTO);
		String otp = Utility.generateOtp();
	    OtpVerification otpVerification = new OtpVerification(user.getId(), otp, LocalDateTime.now(), false);
	    otpRepo.save(otpVerification);
        pushService.sendOtp(user.getDeviceToken(), otp);
		user = userRepository.save(user);
		return userMapper.toDTO(user);
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
}

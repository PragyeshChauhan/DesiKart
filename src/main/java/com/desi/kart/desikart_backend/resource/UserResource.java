package com.desi.kart.desikart_backend.resource;

import com.desi.kart.desikart_backend.notification.MailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.desi.kart.desikart_backend.dto.UserDTO;
import com.desi.kart.desikart_backend.serviceimpl.UserServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserResource {
	
	private final UserServiceImpl userServiceImpl;
	private final MailService mailService;
		
	public UserResource(UserServiceImpl userServiceImpl,MailService mailService) {
		this.mailService = mailService;
		this.userServiceImpl = userServiceImpl;
	}

	@PostMapping
	public ResponseEntity<?> saveUser(@RequestBody UserDTO userDTO){
		return new ResponseEntity<>(userServiceImpl.createUser(userDTO), HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO){
		return new ResponseEntity<>(userServiceImpl.updateUser(userDTO), HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<?> getUserById(@PathVariable String id){
		return new ResponseEntity<>(userServiceImpl.getUser(id), HttpStatus.OK);
	}
	
	@PatchMapping
	public ResponseEntity<?> updateUserStatus(@RequestBody String id){
		return new ResponseEntity<>(userServiceImpl.updateStatus(id), HttpStatus.OK);
	}

	@PostMapping("/forgot-password-email")
	public ResponseEntity<?> sendResetEmail(@RequestParam String email) {
		String token = UUID.randomUUID().toString();
		userServiceImpl.saveResetToken(email,token);
		String resetLink = "https://desikart.com/reset-password?token=" + token;
		mailService.sendResetPasswordEmail(email, resetLink);
		return ResponseEntity.ok("Reset email sent");
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(HttpServletRequest httpServletRequest) {
		try {
			String token = httpServletRequest.getHeader("token");
			String newPassword = httpServletRequest.getHeader("newPassword");
			userServiceImpl.resetPassword(token, newPassword);
			return ResponseEntity.ok("Password reset successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/reset-password")
	public ResponseEntity<?> passwordReset(HttpServletRequest httpServletRequest) {
		try {
			String oldPassword = httpServletRequest.getHeader("oldPassword");
			String newPassword = httpServletRequest.getHeader("newPassword");
			userServiceImpl.userPasswordReset(oldPassword, newPassword);
			return ResponseEntity.ok("Password reset successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}

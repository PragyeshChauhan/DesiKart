package com.desi.kart.desikart_backend.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desi.kart.desikart_backend.dto.UserDTO;
import com.desi.kart.desikart_backend.serviceimpl.UserServiceImpl;

@RestController
@RequestMapping("/api/user")
public class UserResource {
	
	private final UserServiceImpl userServiceImpl;
		
	public UserResource(UserServiceImpl userServiceImpl) {
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
}

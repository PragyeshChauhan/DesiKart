package com.desi.kart.desikart_backend.service;

import com.desi.kart.desikart_backend.dto.UserDTO;

public interface UserService {
	
	 UserDTO createUser(UserDTO userDTO);
	 UserDTO updateUser(UserDTO userDTO);
	 UserDTO getUser(String id);
	 Object updateStatus(String id);
	 void resetPassword(String token, String newPassword);
	 void saveResetToken(String email,String token);
    void userPasswordReset(String oldPassword, String newPassword);
}

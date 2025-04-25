package com.desi.kart.desikart_backend.service;

import com.desi.kart.desikart_backend.dto.UserDTO;

public interface UserService {
	
	public UserDTO createUser(UserDTO userDTO);
	public UserDTO updateUser(UserDTO userDTO);
	public UserDTO getUser(String id);
	public Object updateStatus(String id);

}

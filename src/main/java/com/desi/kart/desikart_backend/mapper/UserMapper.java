package com.desi.kart.desikart_backend.mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.desi.kart.desikart_backend.domain.User;
import com.desi.kart.desikart_backend.dto.UserDTO;

@Component
public class UserMapper {
	
	public UserDTO toDTO(User user) {
		UserDTO userDTO = new UserDTO();
		if(user==null) {
			return null;
		}
		BeanUtils.copyProperties(user, userDTO);
		return userDTO;
	}
	
	
	public User toDomain(UserDTO userDTO) {
		User user = new User();
		if(userDTO==null) {
			return null;
		}
		BeanUtils.copyProperties(userDTO, user);
		return user;
	}

}

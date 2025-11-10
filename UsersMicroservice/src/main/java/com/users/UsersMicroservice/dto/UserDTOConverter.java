package com.users.UsersMicroservice.dto;


import com.users.UsersMicroservice.entities.UserEntity;
import com.users.UsersMicroservice.entities.UserRole;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDTOConverter {

	/**
	 * Esta es la clase donde hacemos en Conversor. Aquí es donde se convierten los datos en DTO
	 */
	
	private final ModelMapper modelMapper;
	
	/**
	 * Method to convert the UserDTO object  that we receive form the API to User object
	 * @param userDto
	 * @return
	 */
	public UserEntity convertToUserReg(UserRegisterDTO userDto){
		
		return modelMapper.map(userDto, UserEntity.class);
	}
	
	public UserEntity convertToUser(UserLoginDTO userDto) {
		
		return modelMapper.map(userDto, UserEntity.class);
	}
	
	public GetUserDTO convertUserEntityToGetUserDTO(UserEntity userEntity) {
		
		//Esta es otra forma de hacerlo, simplmente construimos un objeto GetUserDTO de userEntity
		return GetUserDTO.builder()
				.username(userEntity.getUsername())
				.fullname(userEntity.getFullname())
				.email(userEntity.getFullname())
				.gender(userEntity.getGender())
				.birthday(userEntity.getBirthday())
				.profileImage(userEntity.getProfileImage())
				.roles(userEntity.getRoles().stream()
						.map(UserRole::name)
						.collect(Collectors.toSet())
						)
				.build();
				
	}
}

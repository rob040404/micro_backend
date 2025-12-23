package com.users.UsersMicroservice.dto;


import com.users.UsersMicroservice.entities.UserEntity;
import com.users.UsersMicroservice.entities.UserRole;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * A class for User DTO conversion
 */
@Component
@RequiredArgsConstructor
public class UserDTOConverter {
	
	private final ModelMapper modelMapper;
	
	/**
	 * Method to convert the UserDTO object  that we receive form the API to User object
	 * @param userDto Is an UserRegistrationRequestDTO
	 * @return returns a UserEntity object
	 */
	public UserEntity convertToUserReg(UserRegistrationRequestDTO userDto){
		
		return modelMapper.map(userDto, UserEntity.class);
	}

    /**
     * Method to convert the UserResponseDTO object that we receive
     * @param userDto Is an UserLoginResponseDTO
     * @return returns a UserEntity object
     */
	public UserEntity convertToUser(UserLoginResponseDTO userDto) {
		
		return modelMapper.map(userDto, UserEntity.class);
	}

    /**
     * Here we convert UserEntity object into UserRegistrationResponseDTO.
     * We do it via builder.
     * @param userEntity A UserEntity object
     * @return UserRegistrationResponseDTO
     */
	public UserRegistrationResponseDTO convertUserEntityToGetUserDTO(UserEntity userEntity) {

		return UserRegistrationResponseDTO.builder()
				.username(userEntity.getUsername())
				.fullname(userEntity.getFullname())
				.gender(userEntity.getGender())
				.birthday(userEntity.getBirthday())
				.profileImage(userEntity.getProfileImage())
				.build();
				
	}
}

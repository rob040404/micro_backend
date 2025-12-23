package com.users.UsersMicroservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for user registration response
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserRegistrationResponseDTO {

	private String username;
	private String fullname;
	private String gender;
	protected LocalDate birthday;
	private String profileImage;
	
}

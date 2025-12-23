package com.users.UsersMicroservice.dto;

import com.users.UsersMicroservice.entities.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

/**
 * Login response. We include the information that will be sent to the Security Microservice after successful login.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserLoginResponseDTO {

	private UUID id;
    private String username;
    private String fullname;
	private String email;
    private String password;
	private String profileImage;
    private Set <UserRole> roles;
}

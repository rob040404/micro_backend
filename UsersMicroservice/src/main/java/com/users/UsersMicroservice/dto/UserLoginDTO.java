package com.users.UsersMicroservice.dto;

import com.users.UsersMicroservice.entities.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserLoginDTO {

	private UUID id;
    private String username;
    private String fullname;
	private String email;
	private String password;
	private String profileImage;
    private Set <UserRole> roles;
}

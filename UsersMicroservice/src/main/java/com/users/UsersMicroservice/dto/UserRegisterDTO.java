package com.users.UsersMicroservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UserRegisterDTO {

	private String username;
	private String email;
	private String password;
	private String password2;
	private String fullname;
	private String gender;
	private LocalDate birthday;
	
}

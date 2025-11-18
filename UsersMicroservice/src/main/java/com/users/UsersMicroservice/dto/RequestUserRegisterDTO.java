package com.users.UsersMicroservice.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class RequestUserRegisterDTO {

	private String username;
	private String email;
	private String password;
	private String password2;
	private String fullname;
	private String gender;
	private LocalDate birthday;
	
}

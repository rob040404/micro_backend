package com.users.UsersMicroservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class RequestUserRegisterDTO {

    @NotBlank @NotNull
	private String username;
    @NotBlank @Email
	private String email;
    @NotBlank@Size(min = 3) //change to more
	private String password;
    @NotBlank @Size(min = 3)
	private String password2;
    @NotBlank @NotNull
	private String fullname;
	private String gender;
	private LocalDate birthday;
	
}

package com.security.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO we use to receive the request for log in
 */
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AuthRequestDTO {

    @NotBlank @Email
    private String email;
    @NotBlank @Min(3)
    private String password;
}

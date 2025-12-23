package com.social.SocialMicroservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO to get the username of the user who is sending the follow request. It is done in order to be able to validate with
 * Valid
 */
@Getter @Setter
public class UsernameRequestDTO {

    @NotNull @NotBlank
    private String username;
}

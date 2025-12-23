package com.book.BookMicroservice.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Class to retrieve the User's email for rating
 */
@Getter
@Setter
@AllArgsConstructor
public class UserResponseDTO {

    @NotBlank
    private String username;
}

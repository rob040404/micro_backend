package com.users.UsersMicroservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for list creation request
 */
@Getter @Setter
public class CreateListRequestDTO {

    @NotNull @NotBlank(message = "listname is required")
    private String listName;

}

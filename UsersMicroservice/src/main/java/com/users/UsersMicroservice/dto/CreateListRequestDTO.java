package com.users.UsersMicroservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateListRequestDTO {

    @NotNull @NotBlank
    private String listName;

}

package com.users.UsersMicroservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter @Setter
public class SaveBookDTO {

    @NotNull @NotBlank
    private String userListId;
    @NotNull
    private Long bookId;
}

package com.users.UsersMicroservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * DTO for saving a new book into a list
 */
@Getter @Setter
public class SaveBookRequestDTO {

    @NotNull @NotBlank
    private List<UUID> userListId;
    @NotNull
    private Long bookId;
}

package com.users.UsersMicroservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for saving a new book into a list
 */
@Getter @Setter
public class SaveBookRequestDTO {

    @NotNull(message = "List IDs are required")
    @NotEmpty(message = "At least one list is required")
    private List<UUID> userListId = new ArrayList<>();

    @NotNull(message = "Book ID is required")
    private Long bookId;
}

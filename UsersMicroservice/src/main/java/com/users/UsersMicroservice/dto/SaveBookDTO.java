package com.users.UsersMicroservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter @Setter
public class SaveBookDTO {

    private String userListId;
    private Long bookId;
}

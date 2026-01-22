package com.users.UsersMicroservice.dto;

import lombok.*;

import java.util.UUID;

/**
 * With this DTO we send the names of all the user's current lists, and we also specify if the book sent is present
 * in the list.
 *
 * This is the result we send when the user is in the book sheet of a specific book in the frontend, and we need to send
 * all his lists and if this book is present in some of these lists.
 *
 * Example of response:
 * [
 *  {"uuid-example", "Favoritos", true},
 *  {"uuid-example", "Por leer", false}
 * ]
 */
@NoArgsConstructor @Setter @Getter @AllArgsConstructor
public class SendUsersListsWithBookResponseDTO {
    private UUID listId;
    private String listName;
    private boolean bookIsPresent;
    private long booksCount;
}

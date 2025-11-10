package com.users.UsersMicroservice.exception;

public class ListNotSavedException extends RuntimeException {

    public ListNotSavedException(String listName) {

        super("List not saved " + listName);
    }
}

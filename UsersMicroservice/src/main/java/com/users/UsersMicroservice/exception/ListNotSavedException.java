package com.users.UsersMicroservice.exception;

public class ListNotSavedException extends RuntimeException {

    public ListNotSavedException() {
        super("List not saved. ");
    }
}

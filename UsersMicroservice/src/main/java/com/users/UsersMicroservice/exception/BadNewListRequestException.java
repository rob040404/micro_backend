package com.users.UsersMicroservice.exception;

public class BadNewListRequestException extends RuntimeException {
    public BadNewListRequestException() {

        super("Valid list name required");
    }
}

package com.users.UsersMicroservice.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {

        super(message);
    }
}

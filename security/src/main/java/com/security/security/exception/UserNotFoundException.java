package com.security.security.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found" );
    }
}

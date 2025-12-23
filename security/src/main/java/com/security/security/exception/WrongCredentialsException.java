package com.security.security.exception;

public class WrongCredentialsException extends RuntimeException {
    public WrongCredentialsException() {
        super("Invalid Credentials");
    }
}

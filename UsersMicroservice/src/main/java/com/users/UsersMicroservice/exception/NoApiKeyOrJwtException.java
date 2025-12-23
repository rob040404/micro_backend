package com.users.UsersMicroservice.exception;

public class NoApiKeyOrJwtException extends RuntimeException {
    public NoApiKeyOrJwtException() {

        super("Invalid or missing authentication credentials");
    }
}

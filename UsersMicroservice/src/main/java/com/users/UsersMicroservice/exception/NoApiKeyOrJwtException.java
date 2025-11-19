package com.users.UsersMicroservice.exception;

public class NoApiKeyOrJwtException extends RuntimeException {
    public NoApiKeyOrJwtException() {

        super("Unexisting or wrong API key o JWT");
    }
}

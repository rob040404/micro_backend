package com.users.UsersMicroservice.exception;

public class DataIntegrityException extends RuntimeException {
    public DataIntegrityException(String message) {

        super("Failed to save User in data base: " + message);
    }
}

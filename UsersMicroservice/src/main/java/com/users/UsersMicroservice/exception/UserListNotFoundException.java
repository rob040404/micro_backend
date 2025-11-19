package com.users.UsersMicroservice.exception;

public class UserListNotFoundException extends RuntimeException {
    public UserListNotFoundException(String message) {

        super("Could not find user list");
    }
}

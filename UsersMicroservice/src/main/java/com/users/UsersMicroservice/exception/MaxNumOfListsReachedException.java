package com.users.UsersMicroservice.exception;

public class MaxNumOfListsReachedException extends RuntimeException {
    public MaxNumOfListsReachedException(String message) {
        super(message);
    }
}

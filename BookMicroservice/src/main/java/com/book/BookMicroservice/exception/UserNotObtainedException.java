package com.book.BookMicroservice.exception;

public class UserNotObtainedException extends RuntimeException {
    public UserNotObtainedException(String message) {
        super(message);
    }
}

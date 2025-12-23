package com.book.BookMicroservice.exception;

public class NoApiKeyOrJwtException extends RuntimeException {
    public NoApiKeyOrJwtException() {

        super("Wrong credentials");
    }
}

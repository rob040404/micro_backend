package com.social.SocialMicroservice.exceptions;

public class NoUserWithSuchUserNameException extends RuntimeException {
    public NoUserWithSuchUserNameException(String message) {

        super("No user with such username: " + message);
    }
}

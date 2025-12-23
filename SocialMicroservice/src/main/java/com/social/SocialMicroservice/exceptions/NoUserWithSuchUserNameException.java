package com.social.SocialMicroservice.exceptions;

public class NoUserWithSuchUserNameException extends RuntimeException {
    public NoUserWithSuchUserNameException() {

        super("No user with such username");
    }
}

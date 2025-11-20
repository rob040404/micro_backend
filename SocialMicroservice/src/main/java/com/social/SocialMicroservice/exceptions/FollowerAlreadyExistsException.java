package com.social.SocialMicroservice.exceptions;

public class FollowerAlreadyExistsException extends RuntimeException {
    public FollowerAlreadyExistsException() {

        super("Follower already exists");
    }
}

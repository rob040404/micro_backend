package com.social.SocialMicroservice.exceptions;

public class FollowRequestAlreadyExistsExeption extends RuntimeException {
    public FollowRequestAlreadyExistsExeption(String message) {
        super(message);
    }
}

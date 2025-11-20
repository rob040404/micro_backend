package com.social.SocialMicroservice.exceptions;

public class FollowerNotSavedException extends RuntimeException {
    public FollowerNotSavedException() {

        super("The new follower was not saved");
    }
}

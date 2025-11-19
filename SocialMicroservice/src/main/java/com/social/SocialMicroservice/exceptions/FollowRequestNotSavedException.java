package com.social.SocialMicroservice.exceptions;

public class FollowRequestNotSavedException extends RuntimeException {
    public FollowRequestNotSavedException() {
        super("Follow request not saved");
    }
}

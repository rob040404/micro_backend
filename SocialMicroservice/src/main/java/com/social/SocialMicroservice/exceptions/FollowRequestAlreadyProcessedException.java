package com.social.SocialMicroservice.exceptions;

public class FollowRequestAlreadyProcessedException extends RuntimeException {
    public FollowRequestAlreadyProcessedException() {

        super("This request is not PENDING");
    }
}

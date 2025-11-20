package com.social.SocialMicroservice.exceptions;

public class FollowRequestAlreadyProcessedException extends RuntimeException {
    public FollowRequestAlreadyProcessedException() {

        super("The status of this request is not PENDING");
    }
}

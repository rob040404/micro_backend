package com.social.SocialMicroservice.exceptions;

public class FollowedIdsDontMatchException extends RuntimeException {
    public FollowedIdsDontMatchException(String message) {

        super("The User's id do not match the followed id from the follow request: " +message);
    }
}

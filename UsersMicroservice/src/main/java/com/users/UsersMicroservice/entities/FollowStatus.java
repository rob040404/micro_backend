package com.users.UsersMicroservice.entities;

/**
 * Class needed for kafka events related to follow_requests.
 * One of the attributes is this type of object
 */
public enum FollowStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    CANCELED
}

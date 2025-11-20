package com.users.UsersMicroservice.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FollowAnsweredEvent(
        UUID followerRegisterId,
        UUID followedId,
        UUID followerId,
        LocalDateTime followedSince
) {

}

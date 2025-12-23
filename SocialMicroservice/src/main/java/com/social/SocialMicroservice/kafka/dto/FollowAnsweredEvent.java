package com.social.SocialMicroservice.kafka.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for the Kafka event that is produced
 */
public record FollowAnsweredEvent(
        UUID followerRegisterId,
        UUID followedId,
        UUID followerId,
        Instant followedSince
) {

}

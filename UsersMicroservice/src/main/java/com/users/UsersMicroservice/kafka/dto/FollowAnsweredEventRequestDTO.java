package com.users.UsersMicroservice.kafka.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for receiving Kafka message for Follow Answers
 * @param followerRegisterId
 * @param followedId
 * @param followerId
 * @param followedSince
 */
public record FollowAnsweredEventRequestDTO(
        UUID followerRegisterId,
        UUID followedId,
        UUID followerId,
        Instant followedSince
) {

}

package com.users.UsersMicroservice.kafka.dto;


import com.users.UsersMicroservice.entities.FollowStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for receiving Kafka message for Follow Requests
 * @param requestId
 * @param followerId
 * @param followedId
 * @param status
 * @param createdAt
 * @param updatedAt
 */
public record FollowRequestEventRequestDTO(
        UUID requestId,
        UUID followerId,
        UUID followedId,
        FollowStatus status,
        Instant createdAt,
        Instant updatedAt
) {}

package com.social.SocialMicroservice.kafka.dto;

import com.social.SocialMicroservice.entities.FollowStatus;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for the Kafka event that is produced
 */
public record FollowRequestEvent(
        UUID requestId,
        UUID followerId,
        UUID followedId,
        FollowStatus status,
        Instant createdAt,
        Instant updatedAt
) {}

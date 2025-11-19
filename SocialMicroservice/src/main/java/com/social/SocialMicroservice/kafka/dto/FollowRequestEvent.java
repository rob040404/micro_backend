package com.social.SocialMicroservice.kafka.dto;

import com.social.SocialMicroservice.entities.FollowStatus;

import java.time.LocalDateTime;
import java.util.UUID;


public record FollowRequestEvent(
        UUID requestId,
        UUID followerId,
        UUID followedId,
        FollowStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

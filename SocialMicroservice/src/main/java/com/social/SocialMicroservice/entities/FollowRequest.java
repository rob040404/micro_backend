package com.social.SocialMicroservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This entity is for follow requests. When one user requests to follow another. Here we manage the tatus of the petition
 */
@Entity
@Table(name = "follow_requests")
@EntityListeners(AuditingEntityListener.class)
@Builder @AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class FollowRequest {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Column(name = "followed_id", nullable = false)
    private UUID followedId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FollowStatus status; //ACCEPTED, PENDING, REJECTED, CANCELED

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updatedAt", nullable = false)
    private Instant updatedAt;


}

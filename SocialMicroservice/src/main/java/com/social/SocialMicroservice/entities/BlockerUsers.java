package com.social.SocialMicroservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for blocked users by users
 */
@Entity
@Table(name = "blocked_users")
@EntityListeners(AuditingEntityListener.class)
@Builder @AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class BlockerUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "blocker_id", nullable = false)
    private UUID blockerId;

    @Column(name = "blocked_id", nullable = false)
    private UUID blockedId;

    @CreatedDate
    @Column(name = "blocked_at", nullable = false, updatable = false)
    private LocalDateTime blockedAt;
}

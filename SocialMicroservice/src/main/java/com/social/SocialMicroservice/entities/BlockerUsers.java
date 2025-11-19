package com.social.SocialMicroservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "blocked_users")
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
    @Column(name = "blockedAt")
    private LocalDateTime blockedAt;
}

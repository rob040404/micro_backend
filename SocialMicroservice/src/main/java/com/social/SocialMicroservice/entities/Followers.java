package com.social.SocialMicroservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "followers") @Builder @Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Followers {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Column(name = "followed_id", nullable = false)
    private UUID followedId;

    @CreationTimestamp
    @Column(name = "followed_since")
    private LocalDateTime followedSince;


}

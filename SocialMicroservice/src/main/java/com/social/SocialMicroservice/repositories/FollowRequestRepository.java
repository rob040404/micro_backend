package com.social.SocialMicroservice.repositories;

import com.social.SocialMicroservice.entities.FollowRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, UUID> {
}

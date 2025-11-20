package com.social.SocialMicroservice.repositories;

import com.social.SocialMicroservice.entities.Followers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowersRepository extends JpaRepository<Followers, UUID> {

    boolean existsByFollowedIdAndFollowerId(UUID followedId, UUID followerId);
}

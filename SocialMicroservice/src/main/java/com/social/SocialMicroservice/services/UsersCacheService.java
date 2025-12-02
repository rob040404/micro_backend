package com.social.SocialMicroservice.services;

import com.social.SocialMicroservice.client.UserServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * A class to cache when the user consumer is called so UsersMicroservice is not constantly called
 */
@Service
@Log4j2 @AllArgsConstructor
public class UsersCacheService {

    private final UserServiceClient userServiceClient;

    @Cacheable(value = "userIds", key = "#username", unless = "#result == null")
    public UUID getUserIdByUsername(String username) {
        log.info("Cache MISS - Fetching user ID from UsersMicroservice for: {}", username);
        return userServiceClient.getUserId(username);
    }

    @CacheEvict(value = "userIds", key = "#username")
    public void evictUserCache(String username) {
        log.info("Evicting cache for username: {}", username);
    }

    @CacheEvict(value = "userIds", allEntries = true)
    public void evictAllUserCache() {
        log.info("Evicting all user ID cache");
    }
}

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

    /**
     * This is a method to obtain the user's Id, but when is cached in Redis it returns Stringand it doesn't even get
     * into the method. But when the result is not cached in Redis yet, the process gets into the method and
     * returns UUID. So we make sure it always returns a String and the method that is calling this one will turn it
     * into a UUID.
     * @param username
     */
    @Cacheable(value = "userIds", key = "#username", unless = "#result == null")
    public String getUserIdByUsernameAsString(String username) {
        log.info("Cache MISS - Fetching user ID from UsersMicroservice for: {}", username);
        UUID uuid = userServiceClient.getUserId(username);
        return uuid.toString();
    }




    //Do the same here
    @CacheEvict(value = "userIds", key = "#username")
    public void evictUserCache(String username) {
        log.info("Evicting cache for username: {}", username);
    }

    @CacheEvict(value = "userIds", allEntries = true)
    public void evictAllUserCache() {
        log.info("Evicting all user ID cache");
    }
}

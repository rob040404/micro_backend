package com.users.UsersMicroservice.service;

import com.users.UsersMicroservice.entities.UserEntity;
import com.users.UsersMicroservice.exception.UserNotFoundException;
import com.users.UsersMicroservice.repositories.UserEntityRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * This service class helps us implement caching with Redis
 */
@Service @Log4j2 @RequiredArgsConstructor
public class CacheService {

    private final UserEntityRepository userEntityRepository;

    /**
     * This method checks if there is an answer registered / cached in Redis
     * @param username If this username is cached the answer will be returned from redis, otherwise
     *                 the petition goes to the database
     * @return  User's id in String format, either from the database or Redis if it is cached
     *
     * It is a String return because Redis always returns it in String format but in the database is UUID,
     * but we don't know if the answer will come from redis or the database, so we return it as String and
     * convert it to UUID later
     *
     */
    @Cacheable(value = "userIds", key = "#username", unless = "#result == null")
    public String sendUserIdByUsernameAsString(String username){
        UserEntity user = userEntityRepository.findByUsername(username).
                orElseThrow(UserNotFoundException::new);
        log.info("User id: {} retrieved form database", user.getId());
        log.info("Cache MISS - Fetching user ID for username: {}", username);
        return  user.getId().toString();
    }



    //Do the same here when needed
    @CacheEvict(value = "userIds", key = "#username")
    public void evictUserCache(String username) {
        log.info("Evicting cache for username: {}", username);
    }

    @CacheEvict(value = "userIds", allEntries = true)
    public void evictAllUserCache() {
        log.info("Evicting all user ID cache");
    }
}

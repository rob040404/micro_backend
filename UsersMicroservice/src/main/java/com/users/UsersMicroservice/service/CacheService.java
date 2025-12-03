package com.users.UsersMicroservice.service;

import com.users.UsersMicroservice.entities.UserEntity;
import com.users.UsersMicroservice.exception.UserNotFoundException;
import com.users.UsersMicroservice.repositories.UserEntityRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service @Log4j2 @AllArgsConstructor
public class CacheService {

    private final UserEntityRepository userEntityRepository;

    @Cacheable(value = "userIds", key = "#username", unless = "#result == null")
    public String sendUserIdByUsernameAsString(String username){
        UserEntity user = userEntityRepository.findByUsername(username).
                orElseThrow(()-> new UserNotFoundException("Did not find user with username " + username));
        log.info("User id: {} retrieved form database", user.getId());
        log.info("Cache MISS - Fetching user ID for username: {}", username);

        return  user.getId().toString();
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

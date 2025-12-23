package com.social.SocialMicroservice.client;


import com.social.SocialMicroservice.exceptions.NoUserWithSuchUserNameException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Consumer class where we define the requests to other microservice
 */
@Component
@Log4j2
public class UserServiceClient {

    private final RestClient userRestClient;
    private final String usersApikey;

    public UserServiceClient(RestClient userRestClient, @Value("${app.users.users.apikey}")String usersApikey) {
        this.userRestClient = userRestClient;
        this.usersApikey = usersApikey;
    }

    /**
     * Calls UsersMicroservice to get the user's id
     */
    public UUID getUserId(String username){
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        try {
            return userRestClient
                    .get()
                    .uri("/user/auth/userId/{username}", username) // relative route
                    .header("users_apikey", this.usersApikey)
                    .retrieve()
                    .body(UUID.class);
        } catch (RestClientException e) {
            log.warn("Failed to fetch user ID for username: {}", username, e);
            throw new NoUserWithSuchUserNameException();
        }
    }


}

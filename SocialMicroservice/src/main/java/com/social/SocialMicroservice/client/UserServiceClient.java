package com.social.SocialMicroservice.client;


import com.social.SocialMicroservice.exceptions.NoUserWithSuchUserNameException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;


@Component
@Getter @Log4j2
public class UserServiceClient {

    private final RestClient userRestClient;
    private static final String api_url = "http://localhost:9001";

    private String users_apikey;

    /**
     * Injection of the uesers apikey with @Value.
     * Important: Did not use Lonbock constructor in this case beacouse it does not inject it
     * @param userRestClient
     * @param usersApikey
     */
    public UserServiceClient(RestClient userRestClient, @Value("${app.users.users.apikey}")String usersApikey) {
        this.userRestClient = userRestClient;
        users_apikey = usersApikey;
    }


    public UUID getUserId(String username){
        try {
            return userRestClient
                    .get()
                    .uri("/user/auth/userId/{username}", username) // relative route
                    .header("users_apikey", this.users_apikey)
                    .retrieve()
                    .body(UUID.class);
        } catch (RestClientException e) {
            // Manejo de errores: 404, 500, timeout, etc.
            log.warn("User id not obtained form UsersMicroservice in function getUser()");
            throw new NoUserWithSuchUserNameException(username);
        }
    }


}

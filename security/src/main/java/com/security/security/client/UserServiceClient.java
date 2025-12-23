package com.security.security.client;

import com.security.security.entity.User;
import com.security.security.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.UnknownServiceException;

/**
 * Consumer class where we define the requests to Users Microservice
 */
@Component
@Getter @Log4j2
public class UserServiceClient {

    private final RestClient userRestClient;

    private String api_url;
    private String users_apikey;

    //Do not do it with Lombock constructors because they don't work well with @Valid
    public UserServiceClient(RestClient userRestClient, @Value("${app.users.users.apikey}")String usersApikey,
                             @Value("${app.users.users.api_url}")String apiUrl) {
        this.userRestClient = userRestClient;
        users_apikey = usersApikey;
        api_url = apiUrl;
    }


    /**
     * Method used to call Users Microservice and obtain the user's information like username, id and role.
     * @param email The user's email should be included as a param in order send it to the Users Microservice
     * @return A User class with the user's information
     * @throws RuntimeException if user is not returned from Users Microservice
     */
    public User getUser(String email){
        try {
            return userRestClient
                    .get()
                    .uri("/user/auth/{email}", email) // relative route
                    .header("users_apikey", this.users_apikey)
                    .retrieve()
                    .body(User.class);
        } catch (RestClientException e) {
            // Manejo de errores: 404, 500, timeout, etc.
            log.warn("User id not obtained form UsersMicroservice in function getUser(). With email: {}", email);
            throw new UserNotFoundException();
        }

    }
}

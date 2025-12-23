package com.book.BookMicroservice.client;

import com.book.BookMicroservice.dto.response.UserResponseDTO;
import com.book.BookMicroservice.exception.UserNotObtainedException;
import com.book.BookMicroservice.security.JWTUtil;
import com.book.BookMicroservice.service.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


import java.util.UUID;

/**
 * Consumer class where we define the requests to other microservice
 */
@Component
public class UserServiceClient {

    private final JWTUtil jwtUtil;
    private final RestClient userRestClient;
    private final String usersApikey;

    public UserServiceClient(JWTUtil jwtUtil, RestClient userRestClient, @Value("${app.users.users.apikey}")String usersApikey) {
        this.jwtUtil = jwtUtil;
        this.userRestClient = userRestClient;
        this.usersApikey =usersApikey;
    }

    /**
     * Method used to call Users Microservice and obtain the user's  username
     * @param authentication From which we extract the user details
     * @return The user's username as a String
     */
    public String getUsername(Authentication authentication){

        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID userId = details.getId();

        try {
            UserResponseDTO response = userRestClient
                    .get()
                    .uri("/user/api/users/{id}", userId)
                    .header("users_apikey", this.usersApikey)
                    .retrieve()
                    .body(UserResponseDTO.class);

            return response.getUsername();
        } catch (RestClientException e) {
            throw new UserNotObtainedException("Username was not obtained");
        }
    }




}

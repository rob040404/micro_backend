package com.social.SocialMicroservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration class to set RestClient so it could be used to call other microservices
 */
@Configuration
public class RestClientConfig {

    private final String usersApiUrl;

    public RestClientConfig(@Value("${app.users.users.api_url}") String usersApiUrl){
        this.usersApiUrl = usersApiUrl;
    }

    @Bean
    public RestClient userRestClient() {
        return RestClient.builder()
                .baseUrl(usersApiUrl)
                .build();
    }
}

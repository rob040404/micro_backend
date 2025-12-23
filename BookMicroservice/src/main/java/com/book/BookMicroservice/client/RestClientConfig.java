package com.book.BookMicroservice.client;

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
    private final String socialApiUrl;

    public RestClientConfig(@Value("${app.users.users.api_url}") String apiUrl,
                            @Value("${app.social.social.api_url}") String socialApiUrl) {
        this.usersApiUrl = apiUrl;
        this.socialApiUrl = socialApiUrl;
    }

    @Bean
    public RestClient userRestClient() {
        return RestClient.builder()
                .baseUrl(usersApiUrl)
                .build();
    }

    @Bean
    public RestClient socialRestClient() {
        return RestClient.builder()
                .baseUrl(socialApiUrl)
                .build();
    }

}

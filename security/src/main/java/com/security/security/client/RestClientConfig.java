package com.security.security.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration class to set RestClient so it could be used to call the Users Microservice
 */
@Configuration
public class RestClientConfig {

    private final String api_url;

    public RestClientConfig(@Value("${app.users.users.api_url}") String apiUrl) {
        this.api_url = apiUrl;
    }

    @Bean
    public RestClient userRestClient() {
        return RestClient.builder()
                //.baseUrl("http://user-service") // nombre del servicio (si usas Eureka/Consul)
                // o usa la URL directa: "http://localhost:8081"
                .baseUrl(this.api_url)
                .build();
    }
}

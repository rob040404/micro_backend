package com.security.security.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final String api_url = "http://localhost:9001";

    @Bean
    public RestClient userRestClient() {
        return RestClient.builder()
                //.baseUrl("http://user-service") // nombre del servicio (si usas Eureka/Consul)
                // o usa la URL directa: "http://localhost:8081"
                .baseUrl(api_url)
                .build();
    }
}

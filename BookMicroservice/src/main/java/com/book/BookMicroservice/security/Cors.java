package com.book.BookMicroservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class Cors {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        var source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration baseConfig = new CorsConfiguration();
        baseConfig.setAllowedOrigins(List.of("http://localhost:4200"));
        baseConfig.setAllowedHeaders(List.of("Content-Type", "Authorization", "books_apikey"));
        baseConfig.setAllowCredentials(true);
        baseConfig.setMaxAge(3600L); // 1 hora

        registerCors(source, "/user/auth/vote", baseConfig, "POST");
        registerCors(source, "/user/auth/review", baseConfig, "POST");
        registerCors(source, "/book/search", baseConfig, "POST");
        registerCors(source, "/user/all_reviews/**", baseConfig, "GET");

        return source;
    }

    private void registerCors(
            UrlBasedCorsConfigurationSource source,
            String path,
            CorsConfiguration baseConfig,
            String... methods) {
        CorsConfiguration config = new CorsConfiguration(baseConfig);
        config.setAllowedMethods(Arrays.asList(methods));
        source.registerCorsConfiguration(path, config);
    }
}
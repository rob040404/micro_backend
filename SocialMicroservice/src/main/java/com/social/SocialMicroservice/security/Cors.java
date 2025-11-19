package com.social.SocialMicroservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class Cors {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var source = new UrlBasedCorsConfigurationSource();

        // 🔹 Config para /user/register
        CorsConfiguration userCors = new CorsConfiguration();
        userCors.setAllowedOrigins(List.of("http://localhost:4200"));
        userCors.setAllowedMethods(List.of("POST", "OPTIONS"));
        userCors.setAllowedHeaders(List.of("Content-Type", "Authorization", "users_apikey"));
        userCors.setAllowCredentials(true);
        userCors.setMaxAge(3600L);


        // Registro
        source.registerCorsConfiguration("/user/register", userCors);

        return source;
    }
}

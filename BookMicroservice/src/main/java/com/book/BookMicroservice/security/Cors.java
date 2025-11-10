package com.book.BookMicroservice.security;

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

        // 🔹 Config para /user/auth/vote
        CorsConfiguration voteCors = new CorsConfiguration();
        voteCors.setAllowedOrigins(List.of("http://localhost:4200"));
        voteCors.setAllowedMethods(List.of("POST", "OPTIONS"));
        voteCors.setAllowedHeaders(List.of("Content-Type", "Authorization", "books_apikey"));
        voteCors.setAllowCredentials(true);
        voteCors.setMaxAge(3600L);

        // 🔹 Config para /book/search
        CorsConfiguration bookSearchCors = new CorsConfiguration();
        bookSearchCors.setAllowedOrigins(List.of("http://localhost:4200"));
        bookSearchCors.setAllowedMethods(List.of("POST", "OPTIONS"));
        bookSearchCors.setAllowedHeaders(List.of("Content-Type", "Authorization", "books_apikey"));
        bookSearchCors.setAllowCredentials(true);
        bookSearchCors.setMaxAge(3600L);

        // 🔹 Config para user/auth/review
        CorsConfiguration reviewCors = new CorsConfiguration();
        reviewCors.setAllowedOrigins(List.of("http://localhost:4200"));
        reviewCors.setAllowedMethods(List.of("POST", "OPTIONS"));
        reviewCors.setAllowedHeaders(List.of("Content-Type", "Authorization", "books_apikey"));
        reviewCors.setAllowCredentials(true);
        reviewCors.setMaxAge(3600L);

        CorsConfiguration allReviewsCors = new CorsConfiguration();
        allReviewsCors.setAllowedOrigins(List.of("http://localhost:4200"));
        allReviewsCors.setAllowedMethods(List.of("GET", "OPTIONS"));
        allReviewsCors.setAllowedHeaders(List.of("Content-Type", "Authorization", "books_apikey"));
        allReviewsCors.setAllowCredentials(true);
        allReviewsCors.setMaxAge(3600L);

        source.registerCorsConfiguration("/user/auth/vote", voteCors);
        source.registerCorsConfiguration("/book/search", bookSearchCors);
        source.registerCorsConfiguration("/user/auth/review", reviewCors);
        source.registerCorsConfiguration("/user/all_reviews/**", allReviewsCors);

        return source;
    }
}

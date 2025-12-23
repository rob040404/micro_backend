package com.users.UsersMicroservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration class where we implement the CORS so the APIs can receive requests from the defined frontend sources
 */
@Configuration
public class Cors {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Only allow your trusted frontend(s)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Allow credentials (cookies, auth headers) – requires explicit origins and headers
        configuration.setAllowCredentials(true);

        // Explicitly list allowed headers (no wildcards when credentials are enabled)
        configuration.setAllowedHeaders(List.of(
                "Content-Type",
                "Authorization",
                "users_apikey",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));

        // Allow common HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cache preflight responses for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applied to all routes
        return source;
    }


    /*
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

        // 🔹 Config para /files/**
        CorsConfiguration filesCors = new CorsConfiguration();
        filesCors.setAllowedOrigins(List.of("http://localhost:4200"));
        filesCors.setAllowedMethods(List.of("GET", "OPTIONS"));
        filesCors.setAllowedHeaders(List.of("*"));
        filesCors.setAllowCredentials(true);
        filesCors.setMaxAge(3600L);

        CorsConfiguration getUserIdCors = new CorsConfiguration();
        getUserIdCors.setAllowedOrigins(List.of("http://localhost:4200"));
        getUserIdCors.setAllowedMethods(List.of("GET", "OPTIONS"));
        getUserIdCors.setAllowedHeaders(List.of("*"));
        getUserIdCors.setAllowCredentials(true);
        getUserIdCors.setMaxAge(3600L);

        //Innecesrario. Cors solo se aplica a petidiones del navegador
        CorsConfiguration userLoginCors = new CorsConfiguration();
        userLoginCors.setAllowedOrigins(List.of("http://localhost:4200"));
        userLoginCors.setAllowedMethods(List.of("GET", "OPTIONS"));
        userLoginCors.setAllowedHeaders(List.of("*"));
        userLoginCors.setAllowCredentials(true);
        userLoginCors.setMaxAge(3600L);

        CorsConfiguration userListCors = new CorsConfiguration();
        userListCors.setAllowedOrigins(List.of("http://localhost:4200"));
        userListCors.setAllowedMethods(List.of("GET", "OPTIONS"));
        userListCors.setAllowedHeaders(List.of("*"));
        userListCors.setAllowCredentials(true);
        userListCors.setMaxAge(3600L);

        CorsConfiguration bookListCors = new CorsConfiguration();
        bookListCors.setAllowedOrigins(List.of("http://localhost:4200"));
        bookListCors.setAllowedMethods(List.of("GET", "OPTIONS"));
        bookListCors.setAllowedHeaders(List.of("*"));
        bookListCors.setAllowCredentials(true);
        bookListCors.setMaxAge(3600L);



        // Registro
        source.registerCorsConfiguration("/user/register", userCors);
        source.registerCorsConfiguration("/files/**", filesCors);
        source.registerCorsConfiguration("/api/users/**", getUserIdCors);
        source.registerCorsConfiguration("/user/auth/**", userCors);
        source.registerCorsConfiguration("/user/auth/newlist", userListCors);
        source.registerCorsConfiguration("/user/auth/addBookToList", bookListCors);
        return source;
    }
    */

}

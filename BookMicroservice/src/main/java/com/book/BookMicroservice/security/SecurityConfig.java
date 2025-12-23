package com.book.BookMicroservice.security;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Security Configuration Class
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final ApiFilter apiFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/monitoring",
                                "/error",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v2/api-docs/**"
                        ).permitAll()
                        // Public endpoints
                        .requestMatchers(HttpMethod.OPTIONS, "/user/auth/vote").permitAll()  // <-- esto es clave
                        .requestMatchers(HttpMethod.POST, "/book/search" ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/all_reviews/**").permitAll()
                        // Protected endpoints (require authentication + roles)
                        .requestMatchers(HttpMethod.POST, "/user/auth/vote").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/user/auth/review").hasAnyRole("USER", "ADMIN")
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )


                .addFilterBefore(apiFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //We disabled form authentication
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    // Disables authentication by default. We don't have login in this microservice
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

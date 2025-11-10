package com.security.security.security;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;



@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    @Autowired
    private final CorsConfigurationSource corsConfigurationSource;

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
                                "/v2/api-docs/**",
                                "/user/auth/login" // 👈 Asegúrate de permitir el login sin autenticación

                        )
                        .permitAll()
                        //Todos los requestMatchers indican que no se requier identificación de usuario, pero sí Api Key
                        .requestMatchers(HttpMethod.GET, "/files/**" ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}

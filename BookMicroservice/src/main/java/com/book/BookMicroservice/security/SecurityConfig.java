package com.book.BookMicroservice.security;

import lombok.AllArgsConstructor;
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

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    @Autowired
    private final CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
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
                        .requestMatchers(HttpMethod.POST, "/book/**" ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/user/auth/vote").permitAll()  // <-- esto es clave
                        .requestMatchers(HttpMethod.POST, "/user/auth/vote").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/user/auth/review").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/user/all_reviews/**").permitAll()
                        //.requestMatchers(HttpMethod.POST, "/user/auth/vote").authenticated() // ✅ ahora solo requiere token válido, sin roles
                        .anyRequest().authenticated()
                )


                .addFilterBefore(apiFilter, UsernamePasswordAuthenticationFilter.class)
                //aquí lo pongo con autowired, no con new, porque no puedo inyectar la dependencia en otros sitio. ApiFilter a lo mejor también por eso
               // .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //Desactivamos la autenticación por formulario
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    // Desactiva la autenticación por defecto (no hay login aquí)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

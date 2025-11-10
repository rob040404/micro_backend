package com.book.BookMicroservice.client;

import com.book.BookMicroservice.dto.response.ResponseUserDTO;
import com.book.BookMicroservice.security.JWTUtil;
import com.book.BookMicroservice.service.CustomUserDetails;
import com.book.BookMicroservice.service.Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Getter
@Component
public class UserServiceClient {


    private JWTUtil jwtUtil;

    private final RestClient userRestClient;

    private static final String api_url = "http://localhost:9001";
    private static final String full_api_url = api_url + "/getUserId";
    //private String users_apikey = "kajds324DJSKNDdnjsaDNJWaA12SDDax09324dsDITncsaeu95482"; //Ocultar en variable de entorno

    String users_apikey;

    // Inyección por constructor (mejor práctica). Ase podría usar AllArgsConstruct en vez del constructor manual
    public UserServiceClient(JWTUtil jwtUtil, RestClient userRestClient, @Value("${app.users.users.apikey}")String usersApikey) {
        this.jwtUtil = jwtUtil;
        this.userRestClient = userRestClient;
        this.users_apikey=usersApikey;
    }

    public String getUsername(Authentication authentication){

        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID userId = details.getId();

        try {
            ResponseUserDTO response = userRestClient
                    .get()
                    .uri("/user/api/users/{id}", userId) // ruta relativa
                    .header("users_apikey", this.users_apikey)     // o elimina si usaste defaultHeader
                    .retrieve()
                    .body(ResponseUserDTO.class);

            return response.getUsername();
        } catch (RestClientException e) {
            // Manejo de errores: 404, 500, timeout, etc.
            throw new RuntimeException("Error al obtener el nombre del usuario", e);
        }

    }




}

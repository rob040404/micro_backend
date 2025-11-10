package com.security.security.client;

import com.security.security.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


@Component
@Getter @Log4j2
public class UserServiceClient {

    private final RestClient userRestClient;

    private static final String api_url = "http://localhost:9001";
    private static final String full_api_url = api_url + "/auth";
    //private String users_apikey = "kajds324DJSKNDdnjsaDNJWaA12SDDax09324dsDITncsaeu95482"; //Ocultar en variable de entorno


    private String users_apikey;

    //No hacerlo con Lombock porque en este caso no se asignaría
    public UserServiceClient(RestClient userRestClient, @Value("${app.users.users.apikey}")String usersApikey) {
        this.userRestClient = userRestClient;
        users_apikey = usersApikey;
    }


    public User getUser(String email){
        try {
            return userRestClient
                    .get()
                    .uri("/user/auth/{email}", email) // ruta relativa
                    .header("users_apikey", this.users_apikey)     // o elimina si usaste defaultHeader
                    .retrieve()
                    .body(User.class);
        } catch (RestClientException e) {
            // Manejo de errores: 404, 500, timeout, etc.
            log.warn("User id not obtained form UsersMicroservice in function getUser()");
            throw new RuntimeException("Error al obtener el usuario", e); //Hacer la conf de excepciones
        }

    }
}

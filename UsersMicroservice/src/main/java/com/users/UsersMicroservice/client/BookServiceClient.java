package com.users.UsersMicroservice.client;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class BookServiceClient {

    /*
    private static final String api_url = "http://localhost:9001";
    private static final String get_user_id = api_url + "/getUserId";
    private static final String get_user_id_api_key = "lasdnskalda2323"; //Ocultar en variable de entorno

    public long getUserId(String token){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        Claims claims = Jwts.parser().setSigningKey(get_user_id_api_key).parseClaimsJws(token).getBody();

        RestTemplate restTemplate = new RestTemplate();
        String urlWithToken = get_user_id;
        HttpEntity<Long> requestEntity = new HttpEntity<>(headers);
        ResponseEntity responseEntity;

        try{
            responseEntity= restTemplate.exchange(
                    new URI(urlWithToken),
                    HttpMethod.GET,
                    requestEntity,
                    Long.class
            );
        }catch (URISyntaxException e){
            throw new RuntimeException();
        }

        return responseEntity.getBody(); //normal, falta body


    }

     */
}

package com.book.BookMicroservice.service;

import com.book.BookMicroservice.security.APIKeyAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {

    private static final String token_header = "books_apikey";
    //private static final String authToken = "Adsw32DKW3249756DNLSKWjdnsw214JD13DDSDFqdn65793027hdwhj0293ksn32jkNLJNKknnjJn3232";

    private static String authToken;

    @Value("${app.books.books.apikey}") //Porque Value no funciona en static, hay que poner setter
    public void setAuthToken(String token) {
        AuthenticationService.authToken = token;
    }

    public static APIKeyAuthentication getAuthentication(HttpServletRequest request){

        String apiKey = request.getHeader(token_header);

        if(apiKey == null || !apiKey.equals(authToken)){
            throw new BadCredentialsException("Invalid ApiKey");
        }

        return new APIKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}

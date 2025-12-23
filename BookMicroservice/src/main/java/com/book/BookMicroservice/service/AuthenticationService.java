package com.book.BookMicroservice.service;

import com.book.BookMicroservice.security.APIKeyAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
/**
 * Validates incoming requests by checking a configured API key in a specified HTTP header.
 * If valid, returns an authentication token for Spring Security context.
 */
@Service
@Log4j2
public class AuthenticationService {

    private final String expectedApiKey;
    private final String headerName;

    public AuthenticationService(
            @Value("${app.books.books.apikey}") String expectedApiKey,
            @Value("${app.books.header.apikey}") String headerName
    ){
        this.expectedApiKey=expectedApiKey;
        this.headerName=headerName;
    }

    public APIKeyAuthentication getAuthentication(HttpServletRequest request){
        //Extracts the value of the right header
        String apiKey = request.getHeader(headerName);

        //Validates that it matches the api key
        if(apiKey == null || !apiKey.equals(expectedApiKey)){
            throw new BadCredentialsException("Wrong Credentials");
        }

        return new APIKeyAuthentication(
                apiKey,
                AuthorityUtils.createAuthorityList("ROLE_API_CLIENT")
        );
    }
}

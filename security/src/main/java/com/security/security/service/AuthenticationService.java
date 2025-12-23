package com.security.security.service;

import com.security.security.security.APIKeyAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

/**
 * Validates incoming requests by checking a configured API key in a specified HTTP header.
 * If valid, returns an authentication token for Spring Security context.
 */
@Service
@Log4j2
public class AuthenticationService {

    private final String expectedApiKey;
    private final String apiKeyHeader;


    public AuthenticationService(
            @Value("${app.security.blot.apikey}") String expectedApiKey,
            @Value("${app.security.header.apikey:X-API-Key}") String apiKeyHeader) {
        this.expectedApiKey = expectedApiKey;
        this.apiKeyHeader = apiKeyHeader;
    }


    public APIKeyAuthentication getAuthentication(HttpServletRequest request){
        //Extracts the value of the right header
        String apiKey = request.getHeader(apiKeyHeader);

        //Validates that it matches the api key
        if(apiKey == null || !apiKey.equals(expectedApiKey)){
            log.warn("Invalid Api Key in getAuthentication()");
            throw new BadCredentialsException("Invalid Api Key");
        }

        return new APIKeyAuthentication(
                apiKey,
                AuthorityUtils.createAuthorityList("ROLE_API_CLIENT")
        );
    }
}

package com.social.SocialMicroservice.services;


import com.social.SocialMicroservice.security.APIKeyAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {

    private static final String token_header = "users_apikey";
    //private static final String authToken = "kajds324DJSKNDdnjsaDNJWaA12SDDax09324dsDITncsaeu95482";

    private static String authToken;

    @Value("${app.users.users.apikey}") //Porque Value no funciona en static, hay que poner setter
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

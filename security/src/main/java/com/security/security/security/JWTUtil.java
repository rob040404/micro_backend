package com.security.security.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class with methods that help manage all related to JWT tokens
 */
@Component
public class JWTUtil {

    private final String apiKey;
    private final SecretKey secretKey;

    public JWTUtil(@Value("${app.security.blot.apikey}") String apiKey) {
        this.apiKey = apiKey;
        this.secretKey = Keys.hmacShaKeyFor(
                apiKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * This method generates a new JWT authentication token that will be sent to the frontend
     * @param email We use the email to include it in the token. In this case as subject. But maybe someday the id can be
     *              used. Because if the user changes the email this would be inconsistent.
     *              For now users cannot change their email. We would need to do that whn we star using Google or Amazon
     *              authentication.
     * @param userId It is included in the token as a claim
     * @param roles It is included in the token as a claim
     * @param username It is included in the token as a claim
     * @return The JWT token is returned as a String
     */
    public String generateToken(String email, UUID userId, List<String> roles, String username){

        //Token generation
        return Jwts.builder().setSubject(email)
                .claim("userId", userId)
                .claim("username", username)
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis())) //Moment of creation
                .setExpiration(new Date(System.currentTimeMillis() +3000*60*60)) //Expiration
                .signWith(secretKey, SignatureAlgorithm.HS256) //We sign the token with the signature tha carries a secret key
                .compact();  //Signing the token
    }

}

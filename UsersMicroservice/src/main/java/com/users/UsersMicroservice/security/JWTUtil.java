package com.users.UsersMicroservice.security;


import com.users.UsersMicroservice.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JWTUtil {

    @Value("${app.security.blot.apikey}")
    private String apiKey;

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(apiKey.getBytes());
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /*public Long extractId(String token){

        Claims claims = Jwts.parser()
                .setSigningKey(apiKey) //desserializa
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }
*/
    public String extractId(String token){
        SecretKey key = Keys.hmacShaKeyFor(apiKey.getBytes()); // apiKey debe ser una cadena segura (mínimo 256 bits)

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        return claimsJws.getBody().get("userId", String.class);
    }

    //Otra foma más moderna de obtener los claims que la anterior
    public String extractUsername(String token){
        SecretKey key = Keys.hmacShaKeyFor(apiKey.getBytes()); // apiKey debe ser una cadena segura (mínimo 256 bits)

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        return claimsJws.getBody().get("username", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }  catch (ExpiredJwtException e) {
            throw new TokenExpiredException(); //Este error de expiración no llega al front, porque no pasa por el controlador
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            Object roles = claims.get("roles");
            if (roles instanceof List) {
                return (List<String>) roles;
            }
            return List.of(); // por defecto, sin roles
        });
    }
}

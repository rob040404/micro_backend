package com.book.BookMicroservice.security;

import com.book.BookMicroservice.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.List;
import java.util.function.Function;

@Log4j2
@Component
public class JWTUtil {


    //Change the JWT token, don't use the apikey
    @Value("${app.security.blot.apikey}")
    private String apiKey;

    /**
     * Gets the signing key from the token that was established when the token was built
     * @return returns the key
     */
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(apiKey.getBytes());
    }

    /**
     * Extracts the email. In this case the email is the subject. If we decide to implement OAuth we will
     * have to change it to the id.
     * @param token JWT token received from the authentication header
     * @return user's email as String
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the users id present in the token as a claim
     * @param token JWT token received from the authentication header
     * @return user's id as String (it should be cast to UUID later)
     */
    public String extractId(String token){
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extracts the users username present in the token as a claim
     * @param token JWT token received from the authentication header
     * @return users username as string
     */
    public String extractUsername(String token){
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

    /**
     * Extracts a specific claim (field) from a JWT using a resolver function.
     * This method delegates the claim selection logic to the provided {@code claimsResolver}
     * after retrieving all claims from the token.
     *
     * @param <T> The expected type of the claim value to be returned (e.g., String, Date, Integer).
     * @param token The JWT string from which the claim will be extracted.
     * @param claimsResolver A function that accepts the full set of {@code Claims} and
     * returns the desired claim value of type {@code T}.
     * @return The value of the specific claim, cast to type {@code T}.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Method that validates the token
     * @param token JWT token received from the authentication header
     * @return true or false, the token can be valid or not
     */
    public boolean isTokenValid(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }  catch (ExpiredJwtException e) {
            log.warn("Expired JWT token");
            return false;
        } catch (Exception e) {
            log.warn("Invalid JWT token", e);
            return false;
        }
    }

    /**
     * Method that extracts all claims from the JWT token
     * @param token JWT token received from the authentication header
     * @return all claims within the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Method that extracts the roles from the claim that contains those roles
     * @param token JWT token received from the authentication header
     * @return all user's roles in a list.
     */
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            Object roles = claims.get("roles");
            if (roles instanceof List) {
                return (List<String>) roles;
            }
            return List.of(); // Default: without roles
        });
    }
}

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

@Component
public class JWTUtil {

    //private final String apiKey = "AjskNKlnklnNLnL3234kn07283ANhsbe092d3mksmknNNHHJ3ja81m323nnzaZAAL21dmskv";  //todos los micros lo deben tener para desserializar el token
    private final String apiKey;
    private final SecretKey secretKey;

    public JWTUtil(@Value("${app.security.blot.apikey}") String apiKey) {
        this.apiKey = apiKey;
        this.secretKey = Keys.hmacShaKeyFor(
                apiKey.getBytes(StandardCharsets.UTF_8)
        );

    }

    public String generateToken(String email, UUID userId, List<String> roles, String username){

        //generación del token
        return Jwts.builder().setSubject(email) //cambiar subjet a id ya que algún día el email se puede cambiar? Sobre to si se usa luego identificación con Apple, Google, etc.
                .claim("userId", userId)
                .claim("username", username)
                .claim("roles", roles) // ← Añadimos los roles aquí
                .setIssuedAt(new Date(System.currentTimeMillis())) //momento de cración
                .setExpiration(new Date(System.currentTimeMillis() +1000*60*60)) //expiracion
                //.signWith(SignatureAlgorithm.HS256, apiKey)  //Firma con encriptación y apiKey
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();  //firma del token
    }

    //Cambiar los métodos de abajo
    //No devuelva el email en concreto, sino toda la info que está en sub (email, etc.). es lo que hace .getSubject
    public String extractEmail(String token){

        return Jwts.parser()
                .setSigningKey(apiKey) //desserializa
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(apiKey)
                    .parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}

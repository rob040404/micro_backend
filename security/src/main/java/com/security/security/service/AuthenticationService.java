package com.security.security.service;

import com.security.security.security.APIKeyAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * La clase AuthenticationService es un servicio de autenticación por API Key.
 * Revisa las peticiones entrantes para buscar en la cabecera HTTP "bplot_apikey".
 * Si coincide con el valor esperado (sadsdlnknnlknaskd), devuelve un objeto de autenticación (APIKeyAuthentication).
 * Si no coincide, lanza una excepción de credenciales inválidas
 *
 * Esto sirve para proteger una API sin necesidad de usuarios/contraseñas ni JWT, sino con una clave secreta estática
 * que debe ir en cada petición.
 */
@AllArgsConstructor @Log4j2
public class AuthenticationService {

    // La cabecera HTTP, llamada bplot_apikey, donde se espera recibir la API Key, cuando me hagan las peticiones
    private static final String token_header = "bplot_apikey";
    //El valor de la API Key que la app considera válido, el que tae la cabecera bplot_apikey: "bplot_apikey: sadsdlnknnlknaskd"
    //private static final String authToken = "AjskNKlnklnNLnL3234kn07283ANhsbe092d3mksmknNNHHJ3ja81m323nnzaZAAL21dmsk";

    private static String authToken;

    @Value("${app.security.blot.apikey}") //Porque Value no funciona en static, hayq ue poner setter
    public void setAuthToken(String token) {
        AuthenticationService.authToken = token;
    }

    public static APIKeyAuthentication getAuthentication(HttpServletRequest request){

        //Extrae el valor de la cabecera bplot_apikey
        String apiKey = request.getHeader(token_header);

        //Valida que coincida el token
        if(apiKey == null || !apiKey.equals(authToken)){
            log.warn("Invalid Api Key in getAuthentication()");
            throw new BadCredentialsException("Invalid Api Key");
        }

        //El usuario autenticado no tendrá roles ni privilegios asociados.
        //Es un autenticado “básico” solo por poseer la API Key.
        return new APIKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}

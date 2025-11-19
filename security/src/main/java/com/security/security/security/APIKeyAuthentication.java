package com.security.security.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * AbstractAuthenticationToken es una clase base de Spring Security para representar objetos de autenticación.
 * Sus implementaciones se guardan en el SecurityContext una vez que el usuario ha sido autenticado.
 */
public class APIKeyAuthentication extends AbstractAuthenticationToken {

    //Guarda la API Key con la que se autenticó el cliente. Ese valor será el “principal” (es decir, la identidad).
    //"dslndksajdkl"
    //private String apiKey= "AjskNKlnklnNLnL3234kn07283ANhsbe092d3mksmknNNHHJ3ja81m323nnzaZAAL21dmsk";

    @Value("${app.security.blot.apikey}")
    private final String apiKey;

    //Cuando el token está validado entra aquí. El grand auth puede generar errores, etc.
    public APIKeyAuthentication(String apiKey, Collection <? extends GrantedAuthority> authorities){

        super (authorities);

        this.apiKey=apiKey;

        setAuthenticated(true);
    }

    @Override
    public Object getCredentials(){
        return null;
    }

    //Nos da la autenticación a la API
    @Override
    public Object getPrincipal(){
        return apiKey;
    }
}

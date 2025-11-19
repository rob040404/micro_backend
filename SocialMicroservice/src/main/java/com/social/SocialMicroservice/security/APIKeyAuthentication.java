package com.social.SocialMicroservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class APIKeyAuthentication extends AbstractAuthenticationToken {

   // private String apiKey = "lasdnskalda2323";//Luego cambia de valor creo, poner a ""

    @Value("${app.users.users.apikey}")
    private static String apiKey;

    public APIKeyAuthentication(String apiKey, Collection<? extends GrantedAuthority> authorities){

        super (authorities);

        this.apiKey = apiKey;

        setAuthenticated(true);
    }

    @Override
    public Object getCredentials(){
        return null;
    }

    @Override
    public Object getPrincipal(){
        return apiKey;
    }
}

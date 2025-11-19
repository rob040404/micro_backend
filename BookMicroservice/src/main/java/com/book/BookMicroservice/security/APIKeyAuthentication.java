package com.book.BookMicroservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class APIKeyAuthentication extends AbstractAuthenticationToken {

    //private String apiKey = "Adsw32DKW3249756DNLSKWjdnsw214JD13DDSDFqdn65793027hdwhj0293ksn32jkNLJNKknnjJn3232";


    @Value("${app.books.books.apikey}")
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

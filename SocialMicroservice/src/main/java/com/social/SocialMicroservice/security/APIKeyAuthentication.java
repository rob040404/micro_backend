package com.social.SocialMicroservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class APIKeyAuthentication extends AbstractAuthenticationToken {

    private final String clientProvidedApiKey;

    //When the api key in ApiKeyFilter is validated authentication is set (true). GrantedAuthority can generate errors, etc.
    public APIKeyAuthentication(String clientProvidedApiKey, Collection <? extends GrantedAuthority> authorities){

        super (authorities);

        this.clientProvidedApiKey = clientProvidedApiKey;

        setAuthenticated(true); //Only true if validation success
    }

    @Override
    public Object getCredentials(){
        return null;
    }

    //It gives us the API authentication
    @Override
    public Object getPrincipal(){
        return clientProvidedApiKey;
    }
}

package com.book.BookMicroservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
/**
 * Class that sets the authentication in the SecurityContext.
 * AbstractAuthenticationToken is a Spring Security base class for representing authentication objects.
 * Its implementations are stored in the SecurityContext once the user has been authenticated.
 *
 * This token is created only after the provided API key has been validated
 * against the expected secret, and is then stored in the SecurityContext
 * to authorize subsequent requests.
 */
public class APIKeyAuthentication extends AbstractAuthenticationToken {

    private final String clientProvidedApiKey;

    //When the api key in ApiKeyFilter is validated authentication is set (true). GrantedAuthority can generate errors, etc.
    public APIKeyAuthentication(String apiKey, Collection<? extends GrantedAuthority> authorities){

        super (authorities);

        this.clientProvidedApiKey = apiKey;

        setAuthenticated(true); //Only true if validation success
    }

    @Override
    public Object getCredentials(){
        return null;
    }

    @Override
    public Object getPrincipal(){
        return clientProvidedApiKey;
    }
}

package com.users.UsersMicroservice.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;
/**
 * Security user details implementation for JWT-based authentication.
 * Holds authenticated user information (ID, email, username, roles)
 * without sensitive data like passwords.
 * <p>
 * Immutable and used solely to represent principal identity
 * in Spring Security context after JWT validation.
 */
@RequiredArgsConstructor
@Getter @ToString(exclude = {}) // Sensible fields that should not be serialized
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 6762553280669673728L;

    private final UUID id;
	private final String email;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;


    @Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		  return authorities;
	}

    @Override
    public String getPassword() {
        return "N/A";
    }


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

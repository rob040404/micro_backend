package com.book.BookMicroservice.service;

import java.util.Collection;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Getter @ToString(exclude = {}) // Sensible fields that should not be serialized
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 6762553280669673728L;

    private final UUID id;

    public CustomUserDetails(UUID id,  String username, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
    }

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

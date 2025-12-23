package com.security.security.entity;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class defines the User class as in the Users Microservice but we don't have a Data Base in thus case.
 * It is created to simulate the entity in Users Microservice so we can receive object User when UserServiceClient
 * asks for the users information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

	private static final long serialVersionUID = 6189678452627071360L;

	private UUID id;

	private String username;
	
	private String fullname;

	private String email;

	private String password;

	private String profileImage;

	private Set<UserRole> roles;


	/**
	 * Método que proporciona las autirities de este usuario. Transformamos con map el atributo roles(que está en String) a un objeto SimpleGrantedAuthority()
	 * que es el idoneo para pasar las autorites, por lo visto. El resultados será tipo ROLE_ADMIN o ROLE_USER
	 * Usamos .collect() porque hemos planteado que un usuario pueda tener más de un rol
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.name())).collect(Collectors.toList());
	}

	/**
	 * No vamos a gestionar la expiración de cuentas. De hacerse, se tendría que dar
	 * cuerpo a este método
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * No vamos a gestionar el bloqueo de cuentas. De hacerse, se tendría que dar
	 * cuerpo a este método
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * No vamos a gestionar la expiración de cuentas. De hacerse, se tendría que dar
	 * cuerpo a este método
	 */

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	
	/**
	 * No vamos a gestionar el bloqueo de cuentas. De hacerse, se tendría que dar
	 * cuerpo a este método
	 */	
	@Override
	public boolean isEnabled() {
		return true;
	}

}



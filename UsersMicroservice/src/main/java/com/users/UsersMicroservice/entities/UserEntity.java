package com.users.UsersMicroservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name="users")
@EntityListeners(AuditingEntityListener.class) //Eso sirve para que funcione createdAt, con la fecha de la creación
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements UserDetails {

	//Hacemos esta entidad que es como la de User, pero que usaremos para UserDetails
	private static final long serialVersionUID = 6189678452627071360L;

	//Columnas del useEntity
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
	private UUID id;
    //private Long id;

	@NotNull
	@Column(name = "username", unique = true)
	private String username;

	@Column(name = "fullname", nullable = false)
	private String fullname;
	
	@Column(name = "email", unique = true)
	private String email;
	
	@NotNull //Se activa cuando usas @Valid o @Validated en controladores, servicios, etc. Es diferente a la anotación de abajo
    @Column(name = "password", nullable = false)
	private String password;

    @Column(name = "profile_image")
	private String profileImage;

    @Column(name = "gender")
	private String gender;

    @Column(name = "birthday")
	private LocalDate birthday;
	
	/**
	 * @ElementCollection: Indica que el atributo de la clase representa una colección de elementos no entidades, como listas de valores básicos 
	 * (por ejemplo, Strings o números) o tipos embebidos. 
	 * fetch = FetchType.EAGER: Especifica que la colección debe cargarse inmediatamente junto con la entidad principal (en lugar de hacerlo de forma diferida,
	 *  como con LAZY).
	 *  
	 *  No se podrá eliminar un user directamante desde mysql debido a la relación con la tabla roles, pero sí con userRepository.deleteById(userId);
	 */

	@ElementCollection(fetch = FetchType.EAGER) //Como es una colección
	@Enumerated(EnumType.STRING)	//Almacena la enumereción de la clase enum UserRole como un String
	private Set<UserRole> roles;

	@CreatedDate
    @Column(name = "createdAt")
	private LocalDateTime createdAt;

	@Builder.Default
	private LocalDateTime lastPasswordChangeAt = LocalDateTime.now();

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



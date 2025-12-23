package com.users.UsersMicroservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name="users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter @Setter
public class UserEntity implements UserDetails {

	private static final long serialVersionUID = 6189678452627071360L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
	private UUID id;

	@NotBlank
	@Column(name = "username", unique = true, nullable = false)
	private String username;

    @NotBlank
	@Column(name = "fullname", nullable = false)
	private String fullname;

    @NotBlank @Email
	@Column(name = "email", unique = true, nullable = false)
	private String email;

    @Size(min = 3) //change to more
	@NotBlank //Se activa cuando usas @Valid o @Validated en controladores, servicios, etc. Es diferente a la anotación de abajo
    @Column(name = "password", nullable = false)
	private String password;

    @Column(name = "profile_image")
	private String profileImage;

    @Column(name = "gender")
	private String gender;

    @Column(name = "birthday")
	private LocalDate birthday;


	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
    @Builder.Default //We make roles NotNull by default, it avoids NullPointException
	private Set<UserRole> roles = EnumSet.noneOf(UserRole.class);

	@CreatedDate
    @Column(name = "createdAt")
	private LocalDateTime createdAt;

	private LocalDateTime lastPasswordChangeAt = LocalDateTime.now();


	/**
     * This method provides the user's authority. We transform the `roles` attribute (which is a String)
     * into a `SimpleGrantedAuthority()` object using `map`, which is the appropriate object for
     * passing authority. The result will be of type `ROLE_ADMIN` or `ROLE_USER`. We use `.collect()`
     * because we've specified that a user can have more than one role.
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
                .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.name()))
                .collect(Collectors.toList());
	}

	/**
	 * Method that manages non-expired accounts
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * Method for managing blocked accounts
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * Method that manages expired accounts
	 */

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	
	/**
	 * Method for managing enabled accounts
	 */	
	@Override
	public boolean isEnabled() {
		return true;
	}

    /**
     * Initializes lastPasswordChangedAt before persistence. If it has to be updated do it from the service
     */
    @PrePersist
    protected void onCreate() {
        if (lastPasswordChangeAt == null) {
            lastPasswordChangeAt = LocalDateTime.now();
        }
    }
}



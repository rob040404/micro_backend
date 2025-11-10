package com.users.UsersMicroservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class GetUserDTO {

	//Info que podemos devolver del usuario tras hacer el registro

	private String username;
	private String fullname;
	private String email; // ¡¡Quitar para que email no sea visible (al menos no completo)!!
	private String gender;
	protected LocalDate birthday;
	private Set<String> roles;
	private String profileImage;
	
}

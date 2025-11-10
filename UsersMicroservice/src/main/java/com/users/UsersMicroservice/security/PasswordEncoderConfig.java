package com.users.UsersMicroservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Es bueno separar el PasswordEncoder del SecurityCnfig, porque al manejas UserDetails y otras cosas se puede generar unas inyecciones circulares
 * 
 * PERO DE MOMENTO LO DEJO EN EL SecurityConfig
 */
@Configuration
public class PasswordEncoderConfig {

	@Bean
	 public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Usar BCrypt para codificar contraseñas
    }
	
}

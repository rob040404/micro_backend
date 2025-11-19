package com.users.UsersMicroservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJpaAuditing
public class MyConfiguration {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	// Configuración CORS para permitir acceso desde Angular frontend en localhost:4200

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		
		return new WebMvcConfigurer() {

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				
				registry.addMapping("/user/auth/**")
					.allowedOrigins("http://localhost:4200")
					.allowedMethods("GET", "POST", "OPTIONS")
					.allowedHeaders("*")   // Permite todos los headers
					.allowCredentials(true) 
					.maxAge(3600);
				
				registry.addMapping("/user/register/**")
					.allowedOrigins("http://localhost:4200")
					.allowedMethods("GET", "POST", "OPTIONS")
					.allowedHeaders("*")   // Permite todos los headers
					.allowCredentials(true) 
					.maxAge(3600);

			}
			
		};
	}
}

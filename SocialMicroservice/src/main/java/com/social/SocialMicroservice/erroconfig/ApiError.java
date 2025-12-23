package com.social.SocialMicroservice.erroconfig;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
/**
 * Standard error response structure for REST APIs.
 * Used by global exception handlers to return consistent error formats.
 */
@Setter
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
public class ApiError {

	@NonNull
	private HttpStatus status;
	@JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
	private LocalDateTime date = LocalDateTime.now();
	@NonNull
	private String message;
	
}

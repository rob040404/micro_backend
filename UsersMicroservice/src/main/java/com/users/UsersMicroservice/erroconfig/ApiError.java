package com.users.UsersMicroservice.erroconfig;

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
public class ApiError {

    @NonNull
    private final HttpStatus status;
    @JsonFormat(shape = Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss")
    private final LocalDateTime timestamp;
    @NonNull
    private final String message;

    public ApiError(HttpStatus status, String message) {
        this.status = status;
        this.timestamp = LocalDateTime.now(); //Registers the time when the exception is created
        this.message = message;
    }
}

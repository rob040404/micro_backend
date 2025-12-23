package com.security.security.controller;

import com.security.security.dto.AuthRequestDTO;
import com.security.security.security.JWTUtil;
import com.security.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Rest Controller class. Here we define the endpoints and the responses. THe business logic is developed into
 * service classes.
 * Swagger is used to document the APIs
 */
@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserService userService;


    @Operation(summary = "Users login", description = "Authentication controller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login"),
            @ApiResponse(responseCode = "400", description = "Error login")
    })

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody(description = "DTO de petición",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthRequestDTO.class), examples = @ExampleObject(name = "valid example",
                    value = "{ \"email\": \"robert@robert.com\", \"password\": \"2313213\" }"
            )))
                                       @org.springframework.web.bind.annotation.RequestBody @Valid AuthRequestDTO authRequestDTO){


        log.info("Accessing  /auth/login con {}, {}", authRequestDTO.getEmail(), authRequestDTO.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(authRequestDTO));

    }





}

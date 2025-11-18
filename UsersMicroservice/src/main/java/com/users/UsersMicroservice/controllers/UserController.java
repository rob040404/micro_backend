package com.users.UsersMicroservice.controllers;


import com.users.UsersMicroservice.dto.*;
import com.users.UsersMicroservice.service.UserEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Log4j2
@RestController 
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserEntityService userEntityService;


    //No devolvemos ResponseEntity, porque en este caso sabemos qué se va a devolver, un GetUserDTO o saltará a excepción correspondiente
    @Operation(summary = "User registration controller", description = "Controller that manages new user registrations")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "User already exists"),  //¿Dós 400, solo cuenta el segundo
            @ApiResponse(responseCode = "400", description = "The passwords don't match")
    })
    @PostMapping(value = "/register", consumes=MediaType.MULTIPART_FORM_DATA_VALUE) //Duplicar requestbody  No funciona Valid
    public ResponseEntity<ResponseUserDTO> newUser(@RequestBody  @Valid @RequestPart("new") RequestUserRegisterDTO newUser, @RequestBody @RequestPart("file") MultipartFile file) {

        // Añade este log para verificar qué llega
        log.debug("DTO recibido - username: {}, email: {}",
                newUser.getUsername(), newUser.getEmail());

        log.trace("POST /user/register email: {}", newUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.createUser(newUser, file));
    }


    //Solo de prueba (ver para qué se usa, si es para modificar datos debe ser post
    @GetMapping("/api/users/{id}")
    public ResponseEntity<ResponseUserDTO> sendUserId(@PathVariable UUID id){

        return ResponseEntity.status(HttpStatus.OK).body(userEntityService.sendUser(id));

    }

    @GetMapping("/auth/{email}")
    public ResponseEntity<?> userLogin(@PathVariable String email){

        log.trace("POST /auth/ Entering controller with email: {}", email);
        return ResponseEntity.status(HttpStatus.OK).body(userEntityService.userLogin(email));
    }

    @PostMapping("/auth/newlist")
    public ResponseEntity<?> createList(@org.springframework.web.bind.annotation.RequestBody @Valid CreateListRequestDTO newline, Authentication authentication){

        log.trace("POST /newlist controller: {}", newline.getListName());
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.createList(newline, authentication));
    }

    @PostMapping("/auth/addBookToList")
    public ResponseEntity<?> addToList(@org.springframework.web.bind.annotation.RequestBody SaveBookDTO newBook, Authentication authentication){

        log.trace("POST /add book controller: {}", newBook.getBookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.saveBookToList(newBook, authentication));
    }

}

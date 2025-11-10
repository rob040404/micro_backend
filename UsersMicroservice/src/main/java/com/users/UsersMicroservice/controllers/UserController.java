package com.users.UsersMicroservice.controllers;


import com.users.UsersMicroservice.dto.*;
import com.users.UsersMicroservice.repositories.StorageService;
import com.users.UsersMicroservice.service.UserEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping(value = "/register", consumes=MediaType.MULTIPART_FORM_DATA_VALUE) //Duplicar requestbody
    public ResponseEntity<GetUserDTO> newUser(@RequestBody @RequestPart("new") UserRegisterDTO newUser, @RequestBody @RequestPart("file") MultipartFile file) {


        log.trace("POST /user/register email: {}", newUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.createUser(newUser, file));
    }


    //Solo de prueba (ver para qué se usa, si es para modificar datos debe ser post
    @GetMapping("/api/users/{id}")
    public ResponseEntity<GetUserDTO> sendUserId(@PathVariable UUID id){

        return ResponseEntity.status(HttpStatus.OK).body(userEntityService.sendUser(id));

    }

    @GetMapping("/auth/{email}")
    public ResponseEntity<?> userLogin(@PathVariable String email){

        log.trace("POST /auth/ Entering controller with email: {}", email);
        return ResponseEntity.status(HttpStatus.OK).body(userEntityService.userLogin(email));
    }

    @PostMapping("/auth/newlist")
    public ResponseEntity<?> createList(@org.springframework.web.bind.annotation.RequestBody CreateListRequestDTO newlist, Authentication authentication){

        log.trace("POST /newlist controller: {}", newlist.getListName());
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.createList(newlist, authentication));
    }

    @PostMapping("/auth/addBookToList")

    public ResponseEntity<?> addToList(@org.springframework.web.bind.annotation.RequestBody SaveBookDTO newBook, Authentication authentication){

        log.trace("POST /add book controller: {}", newBook.getBookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.saveBookToList(newBook, authentication));
    }

}

package com.users.UsersMicroservice.controllers;

import com.users.UsersMicroservice.dto.*;
import com.users.UsersMicroservice.exception.BadNewListRequestException;
import com.users.UsersMicroservice.exception.DataIntegrityException;
import com.users.UsersMicroservice.exception.ListNotSavedException;
import com.users.UsersMicroservice.exception.UserNotFoundException;
import com.users.UsersMicroservice.service.UserEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Rest Controller class. Here we define the endpoints and the responses. The business logic is developed into
 * service classes.
 * Swagger is used to document the APIs
 */
@Log4j2
@RestController 
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserEntityService userEntityService;

    @Operation(summary = "User registration controller", description = "Controller that manages new user registrations")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201",
                    description = "User successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRegistrationResponseDTO.class)
                    )),
            @ApiResponse(responseCode = "400",
                    description = "User already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataIntegrityException.class)
            ))
    })
    @PostMapping(value = "/register", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserRegistrationResponseDTO> newUser(@RequestPart("new") @Valid UserRegistrationRequestDTO newUser, @RequestPart("file") MultipartFile file) {

        log.debug("Received DTO  - username: {}, email: {}",
                newUser.getUsername(), newUser.getEmail());

        log.info("POST /user/register email: {}", newUser.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.createUser(newUser, file));
    }

    @Operation(summary = "User info for login controller",
            description = "This controller is called fron the security microservice to send the user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User found and information sent",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserLoginResponseDTO.class)
            )),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserNotFoundException.class)
            ))
    })
    @GetMapping("/auth/{email}")
    public ResponseEntity<UserLoginResponseDTO> userLogin(@PathVariable String email){

        log.trace("GET /auth/ Entering controller with email: {}", email);
        return ResponseEntity.status(HttpStatus.OK).body(userEntityService.userLogin(email));
    }

    @Operation(summary = "User id for other microservices",
            description = "This controller is called from other microservices to send the user's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User found and id sent",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UUID.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserNotFoundException.class)
                    ))
    })
    @GetMapping("/auth/userId/{username}")
    public ResponseEntity<UUID> sendUserId(@PathVariable String username){
        log.trace("GET /auth/ Entering controller with username: {}", username);
        return ResponseEntity.status(HttpStatus.OK).body(userEntityService.sendUserId(username));
    }

    @Operation(summary = "Controller for user's list creation",
            description = "Its purpose is to create a new user's list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "List created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )),
            @ApiResponse(responseCode = "400",
                    description = "List not saved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ListNotSavedException.class)
                    ))
    })
    @PostMapping("/auth/newlist")
    public ResponseEntity<String> createList(@RequestBody @Valid CreateListRequestDTO newline, Authentication authentication){

        log.trace("POST /newlist controller: {}", newline.getListName());
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.createList(newline, authentication));
    }

    @Operation(summary = "Controller to add book to a list",
            description = "Its purpose is to add a book to the user's list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Book added to list",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )),
            @ApiResponse(responseCode = "400",
                    description = "Book not added to list",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadNewListRequestException.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserNotFoundException.class)
                    ))
    })
    @PostMapping("/auth/addBookToList")
    public ResponseEntity<String> addToList(@RequestBody @Valid SaveBookRequestDTO newBook, Authentication authentication){

        log.trace("POST /add book controller: {}", newBook.getBookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.saveBookToList(newBook, authentication));
    }

    @GetMapping("/auth/getUsersListsWithBook/{bookId}")
    public ResponseEntity<List<SendUsersListsWithBookResponseDTO>>
    getUsersListsWithBook(@PathVariable Long bookId, Authentication authentication){

        return ResponseEntity.status(HttpStatus.OK).body(userEntityService.getUsersListsWithBook(authentication, bookId));
    }

    @PostMapping("/auth/createSeveralLists")
    public ResponseEntity<List<SendUsersListsWithBookResponseDTO>> createSeveralLists(@RequestBody List<String> newLists, Authentication authentication){

        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.createSeveralLists(newLists, authentication));
    }

    @PostMapping("/auth/addBookToSeveralLists")
    public ResponseEntity<GenericApiResponse> addBookToSeveralLists(@Valid @RequestBody SaveBookRequestDTO listIds, Authentication authentication){

        log.info("Entering addBookToSeveralLists controller with DTO: {}", listIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityService.addBookToSeveralLists(listIds, authentication));
    }



}

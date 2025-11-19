package com.book.BookMicroservice.controllers;

import com.book.BookMicroservice.dto.*;
import com.book.BookMicroservice.service.BookService;
import com.book.BookMicroservice.service.CustomUserDetails;
import com.book.BookMicroservice.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController  //he sustituido el Controller por RestController
@RequiredArgsConstructor
public class BookController {

	@Autowired
	private final BookDTOConverter bookDTOConverter;

	@Autowired
	private final RatingService ratingService;

    @Autowired
    private final BookService bookService;


	//Hay que implementar que encuentre la nota del usuario sobre este libro
	/**
	 * Method when people search for a book
	 * Returns the book objectDTO
	 * @param petitionDTO
	 * @return 
	 */


    @Operation(summary = "Books Search controller", description = "This controller is used to search for books in different ways: by id, by title, by authors and by title and authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful search"),
            @ApiResponse(responseCode = "400", description = "Books not found")
    })
    @PostMapping(value="/book/search")
    public ResponseEntity<?> searchBook(@RequestBody(
            description = "Review DTO",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GetBookDTO.class),
                    examples = {
                            @ExampleObject(name = "Single book example", value = "{\n{ \"id\": \"8323\", \"title\": \"Count Montecristo\", \"authors\": \"Alexandre Dumas\", \"description\": \"Ambientado ene le Siglo XIX...\", \"title\": \"Count Montecristo\"}\n}"),
                            @ExampleObject(name = "List of books example", value =
                                    "{ Books:[" +
                                        "{ \"id\": \"8323\", \"title\": \"Count Montecristo\", \"authors\": \"Alexandre Dumas\", \"description\": \"Ambientado ene le Siglo XIX...\", \"title\": \"Count Montecristo\"}," +
                                        "{ \"id\": \"8323\", \"title\": \"Count Montecristo\", \"authors\": \"Alexandre Dumas\", \"description\": \"Ambientado ene le Siglo XIX...\", \"title\": \"Count Montecristo\"}" +
                                    "]}")
                    }
            )
    )  @org.springframework.web.bind.annotation.RequestBody GetBookDTO petitionDTO, Authentication authentication) {

        log.trace("Entering endpoint /book/search with title:{}, authors: {}, id: {}", petitionDTO.getTitle(), petitionDTO.getAuthors(), petitionDTO.getId());
        return bookService.bookSearch(petitionDTO, authentication); //Está bien anidado, no debería dar problemas
    }




}

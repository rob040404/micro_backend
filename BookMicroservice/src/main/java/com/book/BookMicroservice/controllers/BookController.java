package com.book.BookMicroservice.controllers;

import com.book.BookMicroservice.dto.*;
import com.book.BookMicroservice.dto.request.BookRequestDTO;
import com.book.BookMicroservice.dto.response.BookResponseDTO;
import com.book.BookMicroservice.service.BookService;
import com.book.BookMicroservice.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class BookController {

	@Autowired
	private final BookDTOConverter bookDTOConverter;

	@Autowired
	private final RatingService ratingService;

    @Autowired
    private final BookService bookService;


	//We need to implement a system that finds the user's rating of this book.
	/**
	 * Method when people search for a book
	 * Returns the book objectDTO
	 * @param petitionDTO
	 * @return 
	 */
    @Operation(summary = "Books Search controller", description = "This controller is used to search for books in different ways: by id, by title, by authors and by title and authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful search"),
            @ApiResponse(responseCode = "404", description = "Books don't match criteria")
    })
    @PostMapping(value="/book/search")
    public ResponseEntity<?> searchBook(@RequestBody(
            description = "Review DTO",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookResponseDTO.class),
                    examples = {
                            @ExampleObject(name = "Single book example", value = """
                                    {
                                       "id":"8323",
                                       "title": "Count Montecristo",
                                       "authors": "Alexandre Dumas",
                                       "description": "Ambientado ene le Siglo XIX..."
                                    }
                                    """),
                                    @ExampleObject(name = "List of books example", value = """
                                    {
                                        Books: [
                                            {
                                                "id": 8323,
                                                "title": "Count Montecristo",
                                                "authors": "Alexandre Dumas",
                                                "description": "Ambientado ene le Siglo XIX..."
                                            },
                                            {
                                                 "id": "8323",
                                                 "title": "Count Montecristo",
                                                 "authors": "Alexandre Dumas",
                                                 "description": "Ambientado ene le Siglo XIX..."
                                            }
                                        ]
                                    """)
                    }
            )
    )  @org.springframework.web.bind.annotation.RequestBody @Valid BookRequestDTO petitionDTO, Authentication authentication) {

        log.trace("Entering endpoint /book/search with title:{}, authors: {}, id: {}", petitionDTO.getTitle(), petitionDTO.getAuthors(), petitionDTO.getId());
        return bookService.bookSearch(petitionDTO, authentication);
    }
}

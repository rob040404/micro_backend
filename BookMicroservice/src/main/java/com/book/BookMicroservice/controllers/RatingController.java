package com.book.BookMicroservice.controllers;


import com.book.BookMicroservice.dto.request.ReviewRequestDTO;
import com.book.BookMicroservice.dto.response.RatingResponseDTO;
import com.book.BookMicroservice.dto.response.ReviewResponseDTO;
import com.book.BookMicroservice.dto.request.RatingRequestDTO;
import com.book.BookMicroservice.exception.BookNotFoundException;
import com.book.BookMicroservice.exception.InvalidRatingException;
import com.book.BookMicroservice.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @Operation(summary = "Vote registration controller", description = "Registration or updating of a vote from a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful vote",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RatingResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Error vote",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookNotFoundException.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Error vote",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InvalidRatingException.class)
                    )
            )
    })
    @PostMapping(value="/user/auth/vote")
    public ResponseEntity<RatingResponseDTO> voteBook(@RequestBody(
            description = "Rating DTO",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RatingRequestDTO.class)
                   )
    ) @org.springframework.web.bind.annotation.RequestBody @Valid RatingRequestDTO vote, Authentication authentication){
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.vote(vote, authentication));
    }

    @Operation(summary = "New review by a user", description = "Controller that registers or updates the user's textual review " +
            "for a specific book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created"),
            @ApiResponse(responseCode = "400", description = "Error review")
    })
    @PostMapping(value="/user/auth/review")
    public ResponseEntity<ReviewResponseDTO> reviewBook(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Review DTO",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReviewResponseDTO.class)
            )
    ) @org.springframework.web.bind.annotation.RequestBody ReviewRequestDTO reviewDTO, Authentication authentication){

        log.trace("Entering in user/auth/review");
        log.debug("Reviewing book ID: {}", reviewDTO.getBookIdDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.reviewBook(reviewDTO, authentication));
    }

    @Operation(summary = "Book's reviews controller",
            description =  "Sends the textual reviews of a specific book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Reviews found and sent",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )),
            @ApiResponse(responseCode = "404",
                    description = "Book not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BookNotFoundException.class)
                    ))
    })
    @GetMapping("/user/all_reviews/{bookId}")
    public ResponseEntity<Page<ReviewResponseDTO>> getAllReviews(@PathVariable Long bookId,
                                                                 @RequestParam(defaultValue = "0") int p1,
                                                                 @RequestParam(defaultValue = "3") int p2)
    {

        log.trace("Entering all_reviews endpoint with book id: {}", bookId);
        return ResponseEntity.status(HttpStatus.OK).body(ratingService.getAllReviews(bookId, p1, p2));
    }
}

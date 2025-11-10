package com.book.BookMicroservice.controllers;

import com.book.BookMicroservice.dto.RatingDTO;
import com.book.BookMicroservice.dto.RatingDTOConverter;
import com.book.BookMicroservice.dto.ReviewDTO;
import com.book.BookMicroservice.entity.Book;
import com.book.BookMicroservice.entity.Rating;
import com.book.BookMicroservice.exception.BookNotFoundException;
import com.book.BookMicroservice.exception.InvalidRatingException;
import com.book.BookMicroservice.exception.VoteConversionException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@Log4j2
@RestController
@RequiredArgsConstructor //Sirve para no tener que declarar las final en el constructor manualmente
public class RatingController {

    @Autowired
    private final RatingService ratingService;


    @Operation(summary = "Vote registration controller", description = "Registration or updating of a vote from a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful vote"),
            @ApiResponse(responseCode = "400", description = "Error vote")
    })
    @PostMapping(value="/user/auth/vote")
    public ResponseEntity<?> voteBook(@RequestBody(
            description = "Rating DTO",
            required = true,
            content = @Content(mediaType = "json/application",
                    schema = @Schema(implementation = RatingDTO.class),
                    examples = @ExampleObject(name = "Valid vote", value = "{{ \"ratingDTO\": \"9\", \"bookIdDTO\": \"82\", \"bookRatingDTO\": \"7.7\", \"numVotesDTO\": \"165\" }}")
            )
    ) @org.springframework.web.bind.annotation.RequestBody RatingDTO vote, Authentication authentication){
        return ratingService.vote(vote, authentication);
    }



    //Puede devolver reviewDTO porque sabemos qué tipo de objeto vamos a devolver
    @Operation(summary = "New review by a user", description = "Controller that register or updates the reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful review"),
            @ApiResponse(responseCode = "400", description = "Error review")
    })
    @PostMapping(value="/user/auth/review")
    public ResponseEntity<ReviewDTO> reviewBook(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Review DTO",
            required = true,
            content = @Content(mediaType = "json/application",
                    schema = @Schema(implementation = ReviewDTO.class),
                    examples = @ExampleObject(name = "Valid review", value = "{{ \"reviewDTO\": \"Great book\", \"bookIdDTO\": \"82\" }}")
            )
    ) @org.springframework.web.bind.annotation.RequestBody ReviewDTO reviewDTO, Authentication authentication){

        log.trace("Entering in user/auth/review");
        log.debug("ReviewDTO: {}  Authentication: {}", reviewDTO, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.reviewBook(reviewDTO, authentication));
    }


    @GetMapping("/user/all_reviews/{bookId}")
    public ResponseEntity<?> getAllReviews(@PathVariable Long bookId, @RequestParam int p1, @RequestParam int p2){

        log.trace("Entering all_reviews endpoint with book id: {}", bookId);
        return ResponseEntity.status(HttpStatus.OK).body(ratingService.getAllReviews(bookId, p1, p2));
    }
}

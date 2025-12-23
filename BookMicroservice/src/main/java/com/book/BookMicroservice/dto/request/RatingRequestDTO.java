package com.book.BookMicroservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO class used to call the controller in order to rate a book
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RatingRequestDTO {

    @NotNull
	private int ratingDTO;
    @NotNull
	private long bookIdDTO;
    @NotNull
    private Double bookRatingDTO;
    private Integer numVotesDTO;
}

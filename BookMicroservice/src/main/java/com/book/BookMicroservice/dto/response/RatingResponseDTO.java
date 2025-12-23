package com.book.BookMicroservice.dto.response;

import lombok.*;

/**
 * DTO used to respond with the new saved rating
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RatingResponseDTO {

	private Integer ratingDTO;
	private Long bookIdDTO;
    private Double bookRatingDTO;
    private Integer numVotesDTO;
}

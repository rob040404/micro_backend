package com.book.BookMicroservice.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RatingDTO {

	private int ratingDTO;
	private long bookIdDTO;
    private Float bookRatingDTO;
    private Integer numVotesDTO;
}

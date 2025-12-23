package com.book.BookMicroservice.dto.response;

import lombok.*;

/**
 * DTO used to return the result of a book search
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class BookResponseDTO {

	private Long id;
	private String title;
	private String authors;
	private String description;
	private String genres;
	private String lang;
	private String image;
	private Double rating;
	private Integer numVotes;
	private Integer userVote;
    private String userReview;
}

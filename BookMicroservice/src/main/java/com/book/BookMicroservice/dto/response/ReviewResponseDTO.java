package com.book.BookMicroservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO used to respond with the new saved review
 */
@Builder @Getter @Setter @AllArgsConstructor
public class ReviewResponseDTO {

	private String reviewDTO;
	private Long bookIdDTO;
    private String username;
	
}

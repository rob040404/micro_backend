package com.book.BookMicroservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * DTO used to respond with the new saved review
 */
@Builder @Getter @Setter @AllArgsConstructor
public class ReviewResponseDTO {

    private UUID id;
	private String reviewDTO;
	private Long bookIdDTO;
    private String username;
	
}

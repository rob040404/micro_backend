package com.book.BookMicroservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO used to make a review from a user and is sent to the controller
 */
@Builder @Getter @Setter @AllArgsConstructor
public class ReviewRequestDTO {

	private String reviewDTO;
	private long bookIdDTO;
    private String username;
	
}

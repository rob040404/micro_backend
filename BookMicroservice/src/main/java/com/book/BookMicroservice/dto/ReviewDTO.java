package com.book.BookMicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder @Getter @Setter @AllArgsConstructor
public class ReviewDTO {

	private String reviewDTO;
	private long bookIdDTO;
    private String username;
	
}

package com.book.BookMicroservice.dto;

import com.book.BookMicroservice.entity.Rating;
import org.springframework.stereotype.Component;



import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewDTOConverter {
	
	public ReviewDTO fromRatingToReviewDTO(Rating rating) {
		
		return ReviewDTO.builder()
				.reviewDTO(rating.getReview())
				.bookIdDTO(rating.getBook().getId())
                .username(rating.getUsername())
				.build();
	}
}

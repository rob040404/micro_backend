package com.book.BookMicroservice.dto;

import com.book.BookMicroservice.dto.response.ReviewResponseDTO;
import com.book.BookMicroservice.entity.Rating;
import org.springframework.stereotype.Component;



import lombok.RequiredArgsConstructor;

/**
 * Class with DTO converters for Books
 */
@Component
@RequiredArgsConstructor
public class ReviewDTOConverter {
	
	public ReviewResponseDTO fromRatingToReviewDTO(Rating rating) {
		
		return ReviewResponseDTO.builder()
				.reviewDTO(rating.getReview())
				.bookIdDTO(rating.getBook().getId())
                .username(rating.getUsername())
				.build();
	}
}

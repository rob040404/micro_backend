package com.book.BookMicroservice.dto;

import com.book.BookMicroservice.dto.request.RatingRequestDTO;
import com.book.BookMicroservice.dto.response.RatingResponseDTO;
import com.book.BookMicroservice.entity.Book;
import com.book.BookMicroservice.entity.Rating;
//import com.bookworld.user.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Class with DTO converters for Books
 */
@Component
@RequiredArgsConstructor
public class RatingDTOConverter {

	public Rating fromDTOtoRating(RatingRequestDTO ratingDTO, UUID userId, Book book, String username) {
		
		return Rating.builder()
					.book(book)
					.userId(userId)
					.rating(ratingDTO.getRatingDTO())
					.reviewDate(LocalDateTime.now())
                    .username(username)
					.build();
		
		
	}
	
	public RatingResponseDTO fromRatingToResponseDTO(Rating rating) {
		
		return RatingResponseDTO.builder()
						.ratingDTO(rating.getRating())
						.bookIdDTO(rating.getBook().getId())
                        .bookRatingDTO(rating.getBook().getRating())
                        .numVotesDTO(rating.getBook().getNumVotes())
						.build();
	}
}

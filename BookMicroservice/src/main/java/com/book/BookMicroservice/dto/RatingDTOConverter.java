package com.book.BookMicroservice.dto;

import com.book.BookMicroservice.entity.Book;
import com.book.BookMicroservice.entity.Rating;
//import com.bookworld.user.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RatingDTOConverter {

	public Rating fromDTOtoRating(RatingDTO ratingDTO, UUID userId, Book book, String username) {
		
		return Rating.builder()
					.book(book)
					.userId(userId)
					.rating(ratingDTO.getRatingDTO())
					.reviewDate(LocalDateTime.now())
                    .username(username)
					.build();
		
		
	}
	
	public RatingDTO fromRatingToDTO(Rating rating) {
		
		return RatingDTO.builder()
						.ratingDTO(rating.getRating())
						.bookIdDTO(rating.getBook().getId())
                        .bookRatingDTO(rating.getBook().getRating())
                        .numVotesDTO(rating.getBook().getNumVotes())
						.build();
	}
}

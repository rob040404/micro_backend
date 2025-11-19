package com.book.BookMicroservice.dto;


import com.book.BookMicroservice.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookDTOConverter {

	public GetBookDTO toGetBookDTO(Book book) {
		
		return GetBookDTO.builder()
				.id(book.getId())
				.title(book.getTitle())
				.authors(book.getAuthors())
				.description(book.getDescription())
				.genres(book.getGenres())
				.lang(book.getLang())
				.image(book.getImage())
				.rating(book.getRating())
				.numVotes(book.getNumVotes())
				.build();
	}
	
	public GetBookDTO toGetOptionalBookDTO(Optional<Book> book, int userVote, String userReview) {
		
		return GetBookDTO.builder()
				.id(book.get().getId())
				.title(book.get().getTitle())
				.authors(book.get().getAuthors())
				.description(book.get().getDescription())
				.genres(book.get().getGenres())
				.lang(book.get().getLang())
				.image(book.get().getImage())
				.rating(book.get().getRating())
				.numVotes(book.get().getNumVotes())
				.userVote(userVote)
                .userReview(userReview)
				.build();
	}

}

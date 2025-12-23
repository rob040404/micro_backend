package com.book.BookMicroservice.dto;


import com.book.BookMicroservice.dto.response.BookResponseDTO;
import com.book.BookMicroservice.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Class with DTO converters for Books
 */
@Component
@RequiredArgsConstructor
public class BookDTOConverter {

	public BookResponseDTO toBookResponseDTO(Book book, int userVote, String usersReview) {
		
		return BookResponseDTO.builder()
				.id(book.getId())
				.title(book.getTitle())
				.authors(book.getAuthors())
				.description(book.getDescription())
				.genres(book.getGenres())
				.lang(book.getLang())
				.image(book.getImage())
				.rating(book.getRating())
				.numVotes(book.getNumVotes())
                .userReview(usersReview)
                .userVote(userVote)
				.build();
	}
	
	public BookResponseDTO toBookResponseListDTO(Book book) {
		
		return BookResponseDTO.builder()
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

}

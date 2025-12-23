package com.book.BookMicroservice.dto.request;

import lombok.*;

/**
 * DTO used to search a book or books, and it's sent to the searchBook controller
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class BookRequestDTO {

	private Long id;
	private String title;
	private String authors;
	private String description;
	private String genres;
	private String lang;
}

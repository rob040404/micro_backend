package com.book.BookMicroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6102984154608034711L;
	
	public BookNotFoundException(long id) {
		
		super("Can not find a Book with this id: " + id);
	}

}

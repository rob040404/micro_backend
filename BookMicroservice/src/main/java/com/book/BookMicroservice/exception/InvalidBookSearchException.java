package com.book.BookMicroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBookSearchException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6233274970995875761L;
	
	public InvalidBookSearchException() {
		
		super("Invalid petition. At least one search parameter ('title' or 'authors') is required.");
	}

}

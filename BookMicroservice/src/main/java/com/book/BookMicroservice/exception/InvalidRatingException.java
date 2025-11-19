package com.book.BookMicroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRatingException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -980332106741730014L;

	public  InvalidRatingException() {
		super("Vote is not between 1 and 10");
	}
}

package com.book.BookMicroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FailedRatingException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3788400372215354652L;
	
	public  FailedRatingException() {
		super("Server failed to save te rating");
	}
}

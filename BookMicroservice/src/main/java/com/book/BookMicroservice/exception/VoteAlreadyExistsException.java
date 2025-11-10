package com.book.BookMicroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class VoteAlreadyExistsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4357866235440153034L;
	
	public VoteAlreadyExistsException() {
		
		super("User has already voted");
	}

}

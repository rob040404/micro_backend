package com.book.BookMicroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class VoteConversionException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4117657644313848126L;

	public VoteConversionException() {
		super("Conversion from DTO failed");
	}
}

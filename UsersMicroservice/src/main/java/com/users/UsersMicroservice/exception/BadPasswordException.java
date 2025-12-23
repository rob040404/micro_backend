package com.users.UsersMicroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class BadPasswordException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	public BadPasswordException() {

        super("Wrong password" );
	}
}

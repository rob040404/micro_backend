package com.users.UsersMicroservice.exception;

public class NewUserWithDifferentPasswordsException extends RuntimeException {

	private static final long serialVersionUID = 6449593327963148093L;


	public NewUserWithDifferentPasswordsException() {

        super("The passwords don't match");
	}
}

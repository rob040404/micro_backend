package com.users.UsersMicroservice.erroconfig;


import com.users.UsersMicroservice.exception.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;
/**
 * Centralized exception handler that converts application exceptions
 * into standardized REST API error responses (via {@link ApiError}).
 * Ensures consistent 4xx/5xx responses without exposing internal details.
 */
@Log4j2
@RestControllerAdvice
public class GlobalControllerAdvice /*extends ResponseEntityExceptionHandler*/{

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex){
		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}
	
	@ExceptionHandler(BadPasswordException.class)
	public ResponseEntity<ApiError> handleBadPasswordException(BadPasswordException ex) {
	    ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
	}

	@ExceptionHandler(NewUserWithDifferentPasswordsException.class)
	public ResponseEntity<ApiError> handleNewUserWithDifferentPasswordsException(NewUserWithDifferentPasswordsException ex){
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

    @ExceptionHandler(BadNewListRequestException.class)
    public ResponseEntity<ApiError> handleBadNewListRequestException(BadNewListRequestException ex){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(ListNotSavedException.class)
    public ResponseEntity<ApiError> handleListNotSavedException(ListNotSavedException ex){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(UserListNotFoundException.class)
    public ResponseEntity<ApiError> handleUserListNotFoundException(UserListNotFoundException ex){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(NoApiKeyOrJwtException.class)
    public ResponseEntity<ApiError> handleNoApiKeyOrJwtException(NoApiKeyOrJwtException ex){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<ApiError> handleDataIntegrityException(DataIntegrityException ex){
        log.warn("Data integrity violation", ex);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex){
        log.warn("", ex);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(MaxNumOfListsReachedException.class)
    public ResponseEntity<ApiError> handleMaxNumOfListsReachedException(MaxNumOfListsReachedException ex){
        log.warn("Max number os lists reached by user", ex);
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    /**
     * Method that captures all validation errors, so we don't have to do BindingResult result any time
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity response with ApiError personalized template
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    // Generic non anticipated errors manager
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }


}

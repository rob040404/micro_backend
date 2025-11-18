package com.users.UsersMicroservice.erroconfig;


import com.users.UsersMicroservice.exception.*;
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

@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler{

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
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(UserListNotFoundException.class)
    public ResponseEntity<ApiError> handleUserListNotFoundException(UserListNotFoundException ex){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(NoApiKeyOrJwtException.class)
    public ResponseEntity<ApiError> handleNoApiKeyOrJwtException(NoApiKeyOrJwtException ex){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleDataIntegrityException(DataIntegrityException ex){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    /**
     * Method that captures all validation errors, so we don't have to do BindingResult result any time
     * @param ex
     * @return
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

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
		 // Convertimos el HttpStatusCode a HttpStatus para usarlo en ApiError
	    HttpStatus status = HttpStatus.resolve(statusCode.value());
	    ApiError apiError = new ApiError(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	    
	    return ResponseEntity.status(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR)
	            .headers(headers)
	            .body(apiError);
	}

}

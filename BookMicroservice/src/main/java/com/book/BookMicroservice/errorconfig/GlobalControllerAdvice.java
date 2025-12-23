package com.book.BookMicroservice.errorconfig;


import com.book.BookMicroservice.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Centralized exception handler that converts application exceptions
 * into standardized REST API error responses (via {@link ApiError}).
 * Ensures consistent 4xx/5xx responses without exposing internal details.
 */
@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler{

	@ExceptionHandler
	public ResponseEntity<ApiError> hanldeInvalidBookSearchException(InvalidBookSearchException ex){
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpiredException(TokenExpiredException ex){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<ApiError> hadleBookNotFoundException(BookNotFoundException ex){
		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}
	
	@ExceptionHandler(InvalidRatingException.class)
	public ResponseEntity<ApiError> hadleInvalidRatingException(InvalidRatingException ex){
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

    @ExceptionHandler(NoApiKeyOrJwtException.class)
    public ResponseEntity<ApiError> handleNoApiKeyOrJwtException(NoApiKeyOrJwtException ex){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(UserNotObtainedException.class)
    public ResponseEntity<ApiError> handleUserNotObtainedException(UserNotObtainedException ex){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
	    HttpStatus status = HttpStatus.resolve(statusCode.value());
	    ApiError apiError = new ApiError(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	    
	    return ResponseEntity.status(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR)
	            .headers(headers)
	            .body(apiError);
	}
	
	
}

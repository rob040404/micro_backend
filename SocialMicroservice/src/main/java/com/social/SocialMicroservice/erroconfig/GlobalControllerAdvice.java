package com.social.SocialMicroservice.erroconfig;

import com.social.SocialMicroservice.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;
/**
 * Centralized exception handler that converts application exceptions
 * into standardized REST API error responses (via {@link ApiError}).
 * Ensures consistent 4xx/5xx responses without exposing internal details.
 */
@RestControllerAdvice
public class GlobalControllerAdvice /*extends ResponseEntityExceptionHandler*/{

    @ExceptionHandler(NoApiKeyOrJwtException.class)
    public ResponseEntity<ApiError> handleNoApiKeyOrJwtException(NoApiKeyOrJwtException ex){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(NoUserWithSuchUserNameException.class)
    public ResponseEntity<ApiError> handleNoUserWithSuchUserNameException(NoUserWithSuchUserNameException ex){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpiredException(TokenExpiredException ex){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(FollowedIdsDontMatchException.class)
    public ResponseEntity<ApiError> handleFollowedIdsDontMatchException(FollowedIdsDontMatchException ex){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<ApiError> handleInvalidUsernameException(InvalidUsernameException ex){
        ApiError apiError = new ApiError((HttpStatus.BAD_REQUEST), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex){
        ApiError apiError = new ApiError((HttpStatus.NOT_FOUND), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(FollowRequestAlreadyExistsExeption.class)
    public ResponseEntity<ApiError> handleFollowRequestAlreadyExistsExeption(FollowRequestAlreadyExistsExeption ex){
        ApiError apiError = new ApiError((HttpStatus.NOT_FOUND), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
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


}

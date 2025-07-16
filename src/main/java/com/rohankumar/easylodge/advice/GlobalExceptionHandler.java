package com.rohankumar.easylodge.advice;

import com.rohankumar.easylodge.dtos.wrapper.ErrorResponse;
import com.rohankumar.easylodge.exceptions.BadRequestException;
import com.rohankumar.easylodge.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {

        log.warn("Resource Not Found Error: {}", ex.getMessage());

        return ResponseEntity.status(NOT_FOUND).body(
                ErrorResponse.error(NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {

        log.warn("Bad Request Error: {}", ex.getMessage());

        return ResponseEntity.status(BAD_REQUEST).body(
                ErrorResponse.error(BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {

        log.warn("Bad Credentials Error: {}", ex.getMessage());

        return ResponseEntity.status(BAD_REQUEST).body(
                ErrorResponse.error(BAD_REQUEST.value(), "Invalid email or password"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {

        log.error("Internal Server Error: {}", ex.getMessage());
        log.error("Error: ", ex.fillInStackTrace());

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorResponse.error(INTERNAL_SERVER_ERROR.value(), "Server Error. Please try again"));
    }
}

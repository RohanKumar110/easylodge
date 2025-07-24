package com.rohankumar.easylodge.advice;

import com.rohankumar.easylodge.dtos.wrapper.ErrorResponse;
import com.rohankumar.easylodge.exceptions.BadRequestException;
import com.rohankumar.easylodge.exceptions.ResourceNotFoundException;
import com.rohankumar.easylodge.exceptions.TokenExpiredException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.core.AuthenticationException;
import java.util.Map;
import java.util.stream.Collectors;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        log.warn("Method Arguments Not Valid Error: {}", ex.getMessage());

        Map<String, Object> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        log.warn("Errors: {}", errors);

        return ResponseEntity.status(BAD_REQUEST).body(
                ErrorResponse.error(BAD_REQUEST.value(), "Validation Error", errors));
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

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {

        log.warn("Missing Servlet Request Parameter Error: "+ex.getMessage());

        String message = "Required Parameter '" + ex.getParameterName() + "' is Missing";
        return ResponseEntity.status(BAD_REQUEST).body(
                ErrorResponse.error(BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {

        log.warn("Http Request Method Not Supported Error: "+ex.getMessage());

        String message = "Request method '" + ex.getMethod() + "' is not supported for this Endpoint";
        return ResponseEntity.status(BAD_REQUEST).body(
                ErrorResponse.error(BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {

        log.warn("No Resource Found Error: {}", ex.getMessage());

        return ResponseEntity.status(NOT_FOUND).body(
                ErrorResponse.error(NOT_FOUND.value(),"The Requested Resource was not Found"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {

        log.warn("Resource Not Found Error: {}", ex.getMessage());

        return ResponseEntity.status(NOT_FOUND).body(
                ErrorResponse.error(NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {

        log.warn("Token Expired Error: {}", ex.getMessage());

        return ResponseEntity.status(UNAUTHORIZED).body(
                ErrorResponse.error(UNAUTHORIZED.value(), "Session Expired"));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {

        log.warn("Jwt Error: {}", ex.getMessage());

        return ResponseEntity.status(UNAUTHORIZED).body(
                ErrorResponse.error(UNAUTHORIZED.value(), "Not Authorized"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest req) {

        log.warn("Authentication Error: {}", ex.getMessage());

        return ResponseEntity.status(UNAUTHORIZED).body(
                ErrorResponse.error(UNAUTHORIZED.value(), "Authentication Required"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {

        log.info("Access Denied Error: "+ex.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(
                ErrorResponse.error(FORBIDDEN.value(), "Access Denied"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {

        log.error("Internal Server Error: {}", ex.getMessage());
        log.error("Error: ", ex.fillInStackTrace());

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorResponse.error(INTERNAL_SERVER_ERROR.value(), "Server Error. Please try again"));
    }
}

package com.rohankumar.easylodge.exceptions;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message){
        super(message);
    }
}

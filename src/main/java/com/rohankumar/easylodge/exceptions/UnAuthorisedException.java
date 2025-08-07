package com.rohankumar.easylodge.exceptions;

public class UnAuthorisedException extends RuntimeException{

    public UnAuthorisedException() {}

    public UnAuthorisedException(String message) {
        super(message);
    }

    public UnAuthorisedException(String message, Throwable cause) {
        super(message, cause);
    }
}

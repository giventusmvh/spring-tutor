package com.gvn.springtutor.exception;

/**
 * Custom exception untuk bad request (400 Bad Request).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}

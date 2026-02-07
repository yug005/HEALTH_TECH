package com.healthtech.exception;

/**
 * Exception thrown for invalid login credentials.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

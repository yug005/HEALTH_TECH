package com.healthtech.exception;

/**
 * Exception thrown for resource conflicts (e.g., double booking).
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

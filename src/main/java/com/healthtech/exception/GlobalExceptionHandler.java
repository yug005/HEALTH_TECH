package com.healthtech.exception;

import com.healthtech.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler - Centralized exception handling
 * using @ControllerAdvice.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
                        ResourceNotFoundException ex) {
                log.error("Resource not found: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ApiResponse<Void>> handleDuplicateResourceException(
                        DuplicateResourceException ex) {
                log.error("Duplicate resource: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiResponse<Void>> handleInvalidCredentialsException(
                        InvalidCredentialsException ex) {
                log.error("Authentication failed: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ApiResponse<Void>> handleConflictException(
                        ConflictException ex) {
                log.error("Conflict: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Void>> handleValidationException(
                        MethodArgumentNotValidException ex) {
                log.error("Validation failed: {}", ex.getMessage());

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.validationError("Validation failed", errors));
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex) {
                log.error("Data integrity violation: {}", ex.getMessage());

                String message = "Data integrity violation. Please check your input.";

                if (ex.getMessage() != null && ex.getMessage().contains("unique")) {
                        message = "This resource already exists or conflicts with existing data.";
                }

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(message));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
                        AccessDeniedException ex) {
                log.error("Access denied: {}", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error("Access denied. Insufficient permissions."));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
                log.error("Unexpected error occurred", ex);

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
        }
}

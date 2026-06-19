package com.nec.middleware.exception;

/**
 * Thrown when the request is semantically invalid (e.g. duplicate, illegal state).
 * Used across all modules.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
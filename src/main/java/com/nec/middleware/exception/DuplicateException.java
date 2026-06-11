package com.nec.middleware.exception;

/**
 * Exception thrown when a duplicate resource is detected.
 */
public class DuplicateException extends RuntimeException {

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
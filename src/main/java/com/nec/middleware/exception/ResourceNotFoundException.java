package com.nec.middleware.exception;

/**
 * Thrown when a requested resource is not found in the system.
 * Used across all modules (hr, finance, rbac, etc.)
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
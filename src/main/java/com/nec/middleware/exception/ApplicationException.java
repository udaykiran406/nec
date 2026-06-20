package com.nec.middleware.exception;

/**
 * Base exception class for the entire NEC Middleware application.
 *
 * <p>All application-specific exceptions extend this class, ensuring consistent
 * handling across all modules (masterdata, rbac, workflows, etc.).
 */
public abstract class ApplicationException extends RuntimeException {

    /**
     * Constructs a new application exception with the specified detail message.
     *
     * @param message human-readable description of the error
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Constructs a new application exception with a message and a root cause.
     *
     * @param message human-readable description of the error
     * @param cause   the underlying cause
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}


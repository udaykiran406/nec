package com.nec.middleware.exception;

/**
 * Thrown when a duplicate resource constraint is violated in the application.
 *
 * <p>This exception is applicable to any resource with unique constraints
 * (roles with duplicate code/name, users with duplicate email/phone, regions with duplicate names, etc.)
 * and is handled globally by {@link GlobalExceptionHandler}.
 *
 * <p>HTTP Status: 409 CONFLICT
 */
public class DuplicateException extends DuplicateRecordException {

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}


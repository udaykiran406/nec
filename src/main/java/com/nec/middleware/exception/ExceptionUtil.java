package com.nec.middleware.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.util.*;

/**
 * Utility class for exception throwing and error introspection helpers.
 *
 * <p>Use the {@code throwXxx} methods to raise domain exceptions from service code.
 * Use the introspection helpers ({@link #getRootCauseMessage}, {@link #sanitizeMessage}, etc.)
 * for logging and error-handling utilities.
 */
public class ExceptionUtil {

    // Prevent instantiation
    private ExceptionUtil() {
        throw new AssertionError("Cannot instantiate ExceptionUtil");
    }

    /**
     * Throws a resource not found exception.
     *
     * @param message error message
     * @throws ResourceNotFoundException always
     */
    public static void throwNotFound(String message) {
        throw new ResourceNotFoundException(message);
    }

    /**
     * Throws a duplicate exception.
     *
     * @param message error message
     * @throws DuplicateException always
     */
    public static void throwDuplicate(String message) {
        throw new DuplicateException(message);
    }

    /**
     * Creates a duplicate exception for a known unique constraint and submitted value.
     *
     * @param constraintName database constraint name
     * @param submittedValue business value that caused the duplicate
     * @return DuplicateException with a sanitized user-facing message
     */
    public static DuplicateException duplicateConstraint(String constraintName, String submittedValue) {
        return new DuplicateException(ConstraintFieldMapper.formatDuplicateMessage(constraintName, submittedValue));
    }

    /**
     * Converts a {@link DataIntegrityViolationException} into a user-friendly duplicate exception.
     *
     * @param ex the database integrity exception
     * @return DuplicateException with a sanitized message
     */
    public static DuplicateException fromDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorMessageMapper.ErrorMessageMapping mapping = ErrorMessageMapper.mapDataIntegrityViolation(ex);
        return new DuplicateException(mapping.getMessage());
    }

    /**
     * Converts a {@link DataIntegrityViolationException} into a duplicate exception using the
     * submitted business value when the expected constraint is matched.
     *
     * @param ex             the database integrity exception
     * @param constraintName expected constraint name
     * @param submittedValue submitted business value from the request
     * @return DuplicateException with a sanitized message
     */
    public static DuplicateException fromDataIntegrityViolation(
            DataIntegrityViolationException ex,
            String constraintName,
            String submittedValue) {
        ErrorMessageMapper.ErrorMessageMapping mapping =
                ErrorMessageMapper.mapDataIntegrityViolation(ex, constraintName, submittedValue);
        return new DuplicateException(mapping.getMessage());
    }

    /**
     * Throws a validation exception.
     *
     * @param message error message
     * @throws ValidationException always
     */
    public static void throwValidation(String message) {
        throw new ValidationException(message);
    }

    /**
     * Throws an enhanced validation exception with field errors.
     *
     * @param message error message
     * @param fieldErrors map of field errors
     * @throws EnhancedValidationException always
     */
    public static void throwValidation(String message, Map<String, String> fieldErrors) {
        throw new EnhancedValidationException(message, fieldErrors);
    }

    /**
     * Throws an enhanced validation exception with field errors and missing fields.
     *
     * @param message error message
     * @param fieldErrors map of field errors
     * @param missingFields list of missing fields
     * @throws EnhancedValidationException always
     */
    public static void throwValidation(String message, Map<String, String> fieldErrors,
                                     List<String> missingFields) {
        throw new EnhancedValidationException(message, fieldErrors, missingFields);
    }

    /**
     * Throws a business rule violation exception.
     *
     * @param message error message
     * @throws BusinessRuleViolationException always
     */
    public static void throwBusinessRuleViolation(String message) {
        throw new BusinessRuleViolationException(message);
    }

    /**
     * Throws a concurrent update exception.
     *
     * @param message error message
     * @throws ConcurrentUpdateException always
     */
    public static void throwConcurrentUpdate(String message) {
        throw new ConcurrentUpdateException(message);
    }

    /**
     * Gets the HTTP status code for an error code.
     *
     * @param errorCode the error code (e.g., "VALIDATION_ERROR")
     * @return the HTTP status code
     */
    public static int getHttpStatus(String errorCode) {
        return switch (errorCode) {
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND.value();
            case "DUPLICATE_RECORD" -> HttpStatus.CONFLICT.value();
            case "CONCURRENT_UPDATE" -> HttpStatus.CONFLICT.value();
            case "ACCESS_DENIED" -> HttpStatus.FORBIDDEN.value();
            case "BUSINESS_RULE_VIOLATION" -> HttpStatus.UNPROCESSABLE_ENTITY.value();
            case "VALIDATION_ERROR", "INVALID_REQUEST", "INVALID_DATA" ->
                HttpStatus.BAD_REQUEST.value();
            default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
        };
    }

    /**
     * Extracts the root cause message from an exception chain.
     *
     * @param ex the exception
     * @return the root cause message
     */
    public static String getRootCauseMessage(Throwable ex) {
        Throwable cause = ex;
        while (cause.getCause() != null && !cause.getCause().equals(cause)) {
            cause = cause.getCause();
        }
        return cause.getMessage() != null ? cause.getMessage() : ex.getMessage();
    }

    /**
     * Gets the stack trace as a string (for debugging/logging only, never in responses).
     *
     * @param ex the exception
     * @return the stack trace as a string
     */
    public static String getStackTraceString(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Sanitizes exception messages to remove sensitive information.
     *
     * @param message the original message
     * @return sanitized message safe to show to users
     */
    public static String sanitizeMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "An error occurred.";
        }

        return message
                .replaceAll("com\\.nec\\..*\\.", "")
                .replaceAll("org\\.springframework\\..*\\.", "")
                .replaceAll("java\\..*\\.", "")
                .replaceAll("\\[.*\\]", "")
                .replaceAll("SQLState.*", "");
    }

    /**
     * Checks if an exception is due to database connectivity.
     *
     * @param ex the exception
     * @return true if likely a database connectivity issue
     */
    public static boolean isDatabaseConnectivityError(Throwable ex) {
        String message = getRootCauseMessage(ex);
        return message != null && (
                message.toLowerCase().contains("connection")
                        || message.toLowerCase().contains("timeout")
                        || message.toLowerCase().contains("pool exhausted")
                        || message.toLowerCase().contains("database unavailable")
        );
    }

    /**
     * Checks if an exception is a constraint violation.
     *
     * @param ex the exception
     * @return true if likely a constraint violation
     */
    public static boolean isConstraintViolation(Throwable ex) {
        String message = getRootCauseMessage(ex);
        return message != null && (
                message.toLowerCase().contains("unique")
                        || message.toLowerCase().contains("foreign key")
                        || message.toLowerCase().contains("check constraint")
        );
    }
}


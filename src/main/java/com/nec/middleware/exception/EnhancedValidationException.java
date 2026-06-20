package com.nec.middleware.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom exception for validation errors with detailed field-level information.
 *
 * <p>Extends ApplicationException to provide:
 * - Field-level validation error messages
 * - Missing mandatory fields tracking
 * - Total error count
 * - Structured error response data
 *
 * <p>Usage:
 * <pre>
 * Map<String, String> errors = new HashMap<>();
 * errors.put("Module (1): Module Code", "Module code must not be blank");
 * throw new EnhancedValidationException("Validation failed", errors);
 * </pre>
 */
public class EnhancedValidationException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    /** Field-level validation errors */
    private final Map<String, String> fieldErrors;

    /** List of missing mandatory fields */
    private final List<String> missingFields;

    /** Total count of validation errors */
    private final int totalErrors;

    /**
     * Constructs an EnhancedValidationException with a message
     *
     * @param message the error message
     */
    public EnhancedValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.missingFields = List.of();
        this.totalErrors = 0;
    }

    /**
     * Constructs an EnhancedValidationException with a message and field errors
     *
     * @param message the error message
     * @param fieldErrors map of field-level errors
     */
    public EnhancedValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
        this.missingFields = List.of();
        this.totalErrors = this.fieldErrors.size();
    }

    /**
     * Constructs an EnhancedValidationException with a message, field errors, and missing fields
     *
     * @param message the error message
     * @param fieldErrors map of field-level errors
     * @param missingFields list of missing mandatory fields
     */
    public EnhancedValidationException(String message, Map<String, String> fieldErrors,
                             List<String> missingFields) {
        super(message);
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
        this.missingFields = missingFields != null ? missingFields : List.of();
        this.totalErrors = this.fieldErrors.size();
    }

    /**
     * Constructs an EnhancedValidationException with a message and a cause
     *
     * @param message the error message
     * @param cause the cause exception
     */
    public EnhancedValidationException(String message, Throwable cause) {
        super(message, cause);
        this.fieldErrors = new HashMap<>();
        this.missingFields = List.of();
        this.totalErrors = 0;
    }

    /**
     * Gets the field-level validation errors
     *
     * @return map of field errors
     */
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * Gets the list of missing mandatory fields
     *
     * @return list of missing fields
     */
    public List<String> getMissingFields() {
        return missingFields;
    }

    /**
     * Gets the total count of validation errors
     *
     * @return total error count
     */
    public int getTotalErrors() {
        return totalErrors;
    }

    /**
     * Checks if there are any validation errors
     *
     * @return true if fieldErrors is not empty
     */
    public boolean hasErrors() {
        return !fieldErrors.isEmpty();
    }

    /**
     * Checks if there are any missing mandatory fields
     *
     * @return true if missingFields is not empty
     */
    public boolean hasMissingFields() {
        return !missingFields.isEmpty();
    }
}


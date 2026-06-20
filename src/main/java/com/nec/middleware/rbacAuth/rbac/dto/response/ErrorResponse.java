package com.nec.middleware.rbacAuth.rbac.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Enhanced error response DTO for detailed error information.
 *
 * <p>Provides a comprehensive error response structure that includes:
 * - Error code (e.g., VALIDATION_ERROR, NOT_FOUND, etc.)
 * - User-friendly message
 * - Field-level validation errors (if applicable)
 * - Missing mandatory fields list
 * - Exception details (only in development mode)
 * - Request tracking information
 *
 * <p>Sample Validation Error Response:
 * <pre>
 * {
 *   "errorCode": "VALIDATION_ERROR",
 *   "message": "Please review the information you entered.",
 *   "timestamp": "2026-06-09T14:30:00",
 *   "errors": {
 *     "Module (1): Module Code": "Module code must not be blank. (Mandatory)",
 *     "Module (1), Group (1): Status": "Status is required. (Mandatory)"
 *   },
 *   "missingMandatoryFields": [
 *     "Module (1): Module Code",
 *     "Module (1), Group (1): Status"
 *   ]
 * }
 * </pre>
 *
 * <p>Sample Duplicate Record Error:
 * <pre>
 * {
 *   "errorCode": "DUPLICATE_RECORD",
 *   "message": "A record with the same information already exists.",
 *   "timestamp": "2026-06-09T14:30:00"
 * }
 * </pre>
 *
 * <p>Sample Not Found Error:
 * <pre>
 * {
 *   "errorCode": "NOT_FOUND",
 *   "message": "The requested information could not be found.",
 *   "timestamp": "2026-06-09T14:30:00"
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * Error code identifier (e.g., VALIDATION_ERROR, DUPLICATE_RECORD, NOT_FOUND)
     */
    private String errorCode;

    /**
     * User-friendly error message (never contains technical details or internal implementation specifics)
     */
    private String message;

    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code
     */
    private Integer statusCode;

    /**
     * Map of field-level validation errors
     * Key: User-friendly field path (e.g., "Module (1): Module Code")
     * Value: User-friendly error message
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> errors;

    /**
     * List of missing mandatory fields (extracted from validation errors)
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> missingMandatoryFields;

    /**
     * Total count of validation errors
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalErrors;

    /**
     * Count of missing mandatory fields
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer requiredFieldsCount;

    /**
     * Additional details (e.g., constraint name, database error context)
     * Only included when relevant
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String details;

    /**
     * Request path that caused the error
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path;

    /**
     * Trace ID for request tracking and debugging
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String traceId;

    /**
     * Exception stack trace (ONLY in development mode, never in production)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String stackTrace;

    /**
     * Creates a simple error response with just success, errorCode, and message
     *
     * @param errorCode the error code
     * @param message the error message
     * @param statusCode the HTTP status code
     * @return ErrorResponse instance
     */
    public static ErrorResponse of(String errorCode, String message, int statusCode) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a validation error response with field-level errors
     *
     * @param message the error message
     * @param errors map of field errors
     * @param missingFields list of missing mandatory fields
     * @param statusCode the HTTP status code
     * @return ErrorResponse instance
     */
    public static ErrorResponse ofValidation(String message, Map<String, String> errors,
                                            List<String> missingFields, int statusCode) {
        return ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .missingMandatoryFields(missingFields)
                .totalErrors(errors != null ? errors.size() : 0)
                .requiredFieldsCount(missingFields != null ? missingFields.size() : 0)
                .build();
    }

    /**
     * Adds a single validation error
     *
     * @param fieldPath the field path
     * @param errorMessage the error message
     * @return this instance (for fluent API)
     */
    public ErrorResponse addError(String fieldPath, String errorMessage) {
        if (this.errors == null) {
            this.errors = new java.util.LinkedHashMap<>();
        }
        this.errors.put(fieldPath, errorMessage);
        this.totalErrors = this.errors.size();
        return this;
    }

    /**
     * Adds a missing mandatory field
     *
     * @param fieldName the field name
     * @return this instance (for fluent API)
     */
    public ErrorResponse addMissingField(String fieldName) {
        if (this.missingMandatoryFields == null) {
            this.missingMandatoryFields = new ArrayList<>();
        }
        if (!this.missingMandatoryFields.contains(fieldName)) {
            this.missingMandatoryFields.add(fieldName);
        }
        this.requiredFieldsCount = this.missingMandatoryFields.size();
        return this;
    }

    /**
     * Sets the exception stack trace (only for non-production environments)
     *
     * @param trace the stack trace
     * @return this instance (for fluent API)
     */
    public ErrorResponse withStackTrace(String trace) {
        this.stackTrace = trace;
        return this;
    }

    /**
     * Sets the request path
     *
     * @param requestPath the request path
     * @return this instance (for fluent API)
     */
    public ErrorResponse withPath(String requestPath) {
        this.path = requestPath;
        return this;
    }

    /**
     * Sets the trace ID for request tracking
     *
     * @param id the trace ID
     * @return this instance (for fluent API)
     */
    public ErrorResponse withTraceId(String id) {
        this.traceId = id;
        return this;
    }
}


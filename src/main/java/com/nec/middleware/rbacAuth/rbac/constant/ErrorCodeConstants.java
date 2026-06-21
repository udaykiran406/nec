package com.nec.middleware.rbacAuth.rbac.constant;

/**
 * API Error Codes and HTTP Status Codes for the NEC Middleware application.
 *
 * This enumeration provides a centralized definition of all possible error codes
 * returned by the API. Each error code is mapped to:
 * - A unique code identifier
 * - An HTTP status code
 * - A user-friendly message
 *
 * <p>Usage:
 * <pre>
 * ErrorResponse response = ErrorResponse.builder()
 *     .errorCode(ErrorCodeConstants.VALIDATION_ERROR.getCode())
 *     .message(ErrorCodeConstants.VALIDATION_ERROR.getMessage())
 *     .httpStatus(ErrorCodeConstants.VALIDATION_ERROR.getStatus())
 *     .build();
 * </pre>
 */
public class ErrorCodeConstants {

    // Prevent instantiation
    private ErrorCodeConstants() {
        throw new AssertionError("Cannot instantiate ErrorCodeConstants");
    }

    /* ============================================
       4xx CLIENT ERRORS (Validation & Request Issues)
       ============================================ */

     /** Validation failed - one or more field validations failed */
     public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
     public static final int VALIDATION_ERROR_STATUS = 400;
     public static final String VALIDATION_ERROR_MSG = "Please review the information you entered.";

     /** Invalid request format - malformed JSON or headers */
     public static final String INVALID_REQUEST = "INVALID_REQUEST";
     public static final int INVALID_REQUEST_STATUS = 400;
     public static final String INVALID_REQUEST_MSG = "The submitted information is not in the correct format.";

     /** Duplicate record - unique constraint violation */
     public static final String DUPLICATE_RECORD = "DUPLICATE_RECORD";
     public static final int DUPLICATE_RECORD_STATUS = 409;
     public static final String DUPLICATE_RECORD_MSG = "This record already exists. Please use another value.";

     /** Record not found - requested resource does not exist */
     public static final String NOT_FOUND = "NOT_FOUND";
     public static final int NOT_FOUND_STATUS = 404;
     public static final String NOT_FOUND_MSG = "Record not found.";

    /** Invalid reference - foreign key constraint violation */
    public static final String INVALID_REFERENCE = "INVALID_REFERENCE";
    public static final int INVALID_REFERENCE_STATUS = 400;
    public static final String INVALID_REFERENCE_MSG = "The selected reference is not valid.";

    /** Missing mandatory fields - required fields are missing */
    public static final String MISSING_REQUIRED_FIELDS = "MISSING_REQUIRED_FIELDS";
    public static final int MISSING_REQUIRED_FIELDS_STATUS = 400;
    public static final String MISSING_REQUIRED_FIELDS_MSG = "One or more mandatory fields are missing.";

    /** Business rule violation - validation failed at business logic */
    public static final String BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION";
    public static final int BUSINESS_RULE_VIOLATION_STATUS = 422;
    public static final String BUSINESS_RULE_VIOLATION_MSG = "The provided information does not meet the required criteria.";

    /* ============================================
       5xx SERVER ERRORS (Data Integrity & Unexpected)
       ============================================ */

     /** Database error - unexpected database operation failure */
     public static final String DATABASE_ERROR = "DATABASE_ERROR";
     public static final int DATABASE_ERROR_STATUS = 500;
     public static final String DATABASE_ERROR_MSG = "A database error occurred. Please try again later.";

     /** Concurrent update - optimistic locking failure */
     public static final String CONCURRENT_UPDATE = "CONCURRENT_UPDATE";
     public static final int CONCURRENT_UPDATE_STATUS = 409;
     public static final String CONCURRENT_UPDATE_MSG = "The information was updated by another user. Please refresh and try again.";

     /** Internal server error - unexpected error */
     public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
     public static final int INTERNAL_SERVER_ERROR_STATUS = 500;
     public static final String INTERNAL_SERVER_ERROR_MSG = "An unexpected error occurred. Please contact support if the problem persists.";

     /** Service unavailable - external service or database down */
     public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
     public static final int SERVICE_UNAVAILABLE_STATUS = 503;
     public static final String SERVICE_UNAVAILABLE_MSG = "The service is temporarily unavailable. Please try again later.";

    /* ============================================
       SUCCESS CODES
       ============================================ */

    public static final String SUCCESS = "SUCCESS";
    public static final int SUCCESS_STATUS = 200;
    public static final String CREATED_MSG = "Operation completed successfully.";
    public static final String UPDATED_MSG = "Record updated successfully.";
    public static final String DELETED_MSG = "Record deleted successfully.";
    public static final String RETRIEVED_MSG = "Information retrieved successfully.";

    /* ============================================
       CONSTRAINT VIOLATION MESSAGES
       ============================================ */

    /** Generic unique constraint violation message */
    public static final String UNIQUE_CONSTRAINT_MSG = "This record already exists. Please ensure all unique values are different.";

     /** Password mismatch */
     public static final String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
     public static final String PASSWORD_MISMATCH_MSG = "New password and confirm password do not match";

     /** Duplicate email address */
     public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
     public static final String DUPLICATE_EMAIL_MSG = "Email already exists. Please use another one.";

     /** Duplicate phone number */
     public static final String DUPLICATE_PHONE_MSG = "Phone Number already exists. Please use another one.";

     /** Duplicate role code */
     public static final String DUPLICATE_ROLE_CODE_MSG = "Role Code already exists. Please use another one.";

     /** Duplicate role name */
     public static final String DUPLICATE_ROLE_NAME_MSG = "Role Name already exists. Please use another one.";

     /** Duplicate module code */
     public static final String DUPLICATE_MODULE_CODE_MSG = "Module Code already exists. Please use another one.";

     /** Duplicate group code */
     public static final String DUPLICATE_GROUP_CODE_MSG = "Group Code already exists. Please use another one.";

     /** Duplicate permission code */
     public static final String DUPLICATE_PERMISSION_CODE_MSG = "Permission Code already exists. Please use another one.";

     /** Foreign key constraint violation */
     public static final String FOREIGN_KEY_VIOLATION_MSG = "The selected reference is not valid.";

     /** Check constraint violation */
     public static final String CHECK_CONSTRAINT_VIOLATION_MSG = "The provided information does not meet the required criteria.";
}


package com.nec.middleware.exception;

import com.nec.middleware.rbacAuth.rbac.constant.ErrorCodeConstants;

/**
 * Thrown when a business-level validation rule is violated in the application.
 *
 * <p>HTTP Status: 400 BAD_REQUEST
 */
public class ValidationException extends ApplicationException {

    private final String errorCode;

    public ValidationException(String message) {
        this(ErrorCodeConstants.VALIDATION_ERROR, message);
    }

    public ValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ValidationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

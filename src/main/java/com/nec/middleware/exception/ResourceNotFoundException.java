package com.nec.middleware.exception;

import com.nec.middleware.rbacAuth.rbac.constant.ErrorCodeConstants;

/**
 * Thrown when a requested resource cannot be found in the application.
 *
 * <p>HTTP Status: 404 NOT_FOUND
 */
public class ResourceNotFoundException extends ApplicationException {

    private final String errorCode;

    public ResourceNotFoundException(String message) {
        this(ErrorCodeConstants.NOT_FOUND, message);
    }

    public ResourceNotFoundException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

package com.nec.middleware.exception;

import com.nec.middleware.rbacAuth.rbac.constant.ErrorCodeConstants;

/**
 * Thrown when a business rule or domain constraint is violated.
 * Handled globally as HTTP 400 with the actual exception message.
 */
public class BusinessException extends ApplicationException {

    private final String errorCode;

    public BusinessException(String message) {
        this(ErrorCodeConstants.BUSINESS_RULE_VIOLATION, message);
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode != null ? errorCode : ErrorCodeConstants.BUSINESS_RULE_VIOLATION;
    }
}

package com.nec.middleware.exception;

import com.nec.middleware.rbacAuth.rbac.constant.ErrorCodeConstants;

/**
 * Thrown when a duplicate resource constraint is violated.
 * Handled globally as HTTP 409 with the actual exception message.
 */
public class DuplicateRecordException extends BusinessException {

    public DuplicateRecordException(String message) {
        super(ErrorCodeConstants.DUPLICATE_RECORD, message);
    }

    public DuplicateRecordException(String errorCode, String message) {
        super(errorCode, message);
    }

    public DuplicateRecordException(String message, Throwable cause) {
        super(ErrorCodeConstants.DUPLICATE_RECORD, message, cause);
    }
}

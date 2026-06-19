package com.nec.middleware.exception;

import com.nec.middleware.rbacAuth.rbac.constant.ErrorCodeConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

/**
 * Maps database and technical exceptions to user-friendly error messages and error codes.
 */
public class ErrorMessageMapper {

    public static class ErrorMessageMapping {
        private final String errorCode;
        private final String message;
        private final int httpStatus;

        public ErrorMessageMapping(String errorCode, String message, int httpStatus) {
            this.errorCode = errorCode;
            this.message = message;
            this.httpStatus = httpStatus;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getMessage() {
            return message;
        }

        public int getHttpStatus() {
            return httpStatus;
        }

        public HttpStatus getHttpStatusEnum() {
            return HttpStatus.valueOf(httpStatus);
        }
    }

    private ErrorMessageMapper() {
        throw new AssertionError("Cannot instantiate ErrorMessageMapper");
    }

    /**
     * Maps {@link DataIntegrityViolationException} to a user-friendly error message.
     */
    public static ErrorMessageMapping mapDataIntegrityViolation(DataIntegrityViolationException ex) {
        String combinedMessage = collectExceptionMessages(ex);

        String userFriendlyMessage = ConstraintFieldMapper.resolveDuplicateMessage(combinedMessage);
        if (userFriendlyMessage != null) {
            return duplicateMapping(userFriendlyMessage);
        }

        if (isUniqueConstraint(combinedMessage)) {
            return duplicateMapping(ErrorCodeConstants.DUPLICATE_RECORD_MSG);
        }

        if (isForeignKeyConstraint(combinedMessage)) {
            return new ErrorMessageMapping(
                    ErrorCodeConstants.INVALID_REFERENCE,
                    ErrorCodeConstants.INVALID_REFERENCE_MSG,
                    ErrorCodeConstants.INVALID_REFERENCE_STATUS
            );
        }

        if (isCheckConstraint(combinedMessage)) {
            return new ErrorMessageMapping(
                    ErrorCodeConstants.BUSINESS_RULE_VIOLATION,
                    ErrorCodeConstants.CHECK_CONSTRAINT_VIOLATION_MSG,
                    ErrorCodeConstants.BUSINESS_RULE_VIOLATION_STATUS
            );
        }

        return new ErrorMessageMapping(
                ErrorCodeConstants.DATABASE_ERROR,
                "We could not process your request at this time. Please try again later.",
                ErrorCodeConstants.DATABASE_ERROR_STATUS
        );
    }

    /**
     * Maps a known constraint and submitted value to a duplicate message.
     * Falls back to {@link #mapDataIntegrityViolation(DataIntegrityViolationException)} when the
     * exception does not match the expected constraint.
     */
    public static ErrorMessageMapping mapDataIntegrityViolation(
            DataIntegrityViolationException ex,
            String constraintName,
            String submittedValue) {

        if (constraintName != null && matchesConstraint(ex, constraintName)) {
            return duplicateMapping(ConstraintFieldMapper.formatDuplicateMessage(constraintName, submittedValue));
        }

        return mapDataIntegrityViolation(ex);
    }

    public static boolean matchesConstraint(DataIntegrityViolationException ex, String constraintName) {
        if (constraintName == null || constraintName.isBlank()) {
            return false;
        }
        return collectExceptionMessages(ex).toLowerCase().contains(constraintName.toLowerCase());
    }

    public static ErrorMessageMapping mapOptimisticLockingFailure(
            ObjectOptimisticLockingFailureException ex) {
        return new ErrorMessageMapping(
                ErrorCodeConstants.CONCURRENT_UPDATE,
                ErrorCodeConstants.CONCURRENT_UPDATE_MSG,
                ErrorCodeConstants.CONCURRENT_UPDATE_STATUS
        );
    }

    public static ErrorMessageMapping mapGenericException(Exception ex) {
        String message = collectExceptionMessages(ex);

        if (message.contains("connection") || message.contains("timeoutexception")) {
            return new ErrorMessageMapping(
                    ErrorCodeConstants.INTERNAL_SERVER_ERROR,
                    ErrorCodeConstants.SERVICE_UNAVAILABLE_MSG,
                    ErrorCodeConstants.SERVICE_UNAVAILABLE_STATUS
            );
        }

        return new ErrorMessageMapping(
                ErrorCodeConstants.INTERNAL_SERVER_ERROR,
                ErrorCodeConstants.INTERNAL_SERVER_ERROR_MSG,
                ErrorCodeConstants.INTERNAL_SERVER_ERROR_STATUS
        );
    }

    public static String collectExceptionMessages(Throwable ex) {
        StringBuilder builder = new StringBuilder();
        Throwable current = ex;
        while (current != null) {
            if (current.getMessage() != null && !current.getMessage().isBlank()) {
                if (!builder.isEmpty()) {
                    builder.append(' ');
                }
                builder.append(current.getMessage());
            }
            Throwable next = current.getCause();
            if (next == null || next == current) {
                break;
            }
            current = next;
        }
        return builder.toString().toLowerCase();
    }

    private static ErrorMessageMapping duplicateMapping(String message) {
        return new ErrorMessageMapping(
                ErrorCodeConstants.DUPLICATE_RECORD,
                message,
                ErrorCodeConstants.DUPLICATE_RECORD_STATUS
        );
    }

    private static boolean isUniqueConstraint(String errorMessage) {
        return errorMessage.contains("unique constraint")
                || errorMessage.contains("unique index")
                || errorMessage.contains("duplicate key")
                || errorMessage.contains("duplicate entry")
                || errorMessage.contains("uq_");
    }

    private static boolean isForeignKeyConstraint(String errorMessage) {
        return errorMessage.contains("foreign key")
                || errorMessage.contains("fk_")
                || errorMessage.contains("referential integrity");
    }

    private static boolean isCheckConstraint(String errorMessage) {
        return errorMessage.contains("check constraint")
                || errorMessage.contains("chk_");
    }
}

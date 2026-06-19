package com.nec.middleware.exception;

/**
 * Custom exception for business rule violations.
 *
 * <p>Thrown when a business logic constraint is violated that doesn't fall into other
 * categories like validation, not found, or duplicate.
 *
 * <p>Examples:
 * - Cannot delete a record that is in use
 * - Cannot modify system records
 * - Invalid state transition
 *
 * <p>Usage:
 * <pre>
 * if (role.isSystemRole()) {
 *     throw new BusinessRuleViolationException("You cannot modify system records.");
 * }
 * </pre>
 */
public class BusinessRuleViolationException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    /** The business rule that was violated */
    private final String businessRule;

    /**
     * Constructs a BusinessRuleViolationException with a message
     *
     * @param message the error message
     */
    public BusinessRuleViolationException(String message) {
        super(message);
        this.businessRule = null;
    }

    /**
     * Constructs a BusinessRuleViolationException with a message and business rule
     *
     * @param message the error message
     * @param businessRule the business rule that was violated
     */
    public BusinessRuleViolationException(String message, String businessRule) {
        super(message);
        this.businessRule = businessRule;
    }

    /**
     * Constructs a BusinessRuleViolationException with a message and cause
     *
     * @param message the error message
     * @param cause the cause exception
     */
    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
        this.businessRule = null;
    }

    /**
     * Gets the business rule that was violated
     *
     * @return the business rule description
     */
    public String getBusinessRule() {
        return businessRule;
    }
}


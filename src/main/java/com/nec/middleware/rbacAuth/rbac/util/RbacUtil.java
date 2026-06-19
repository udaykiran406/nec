package com.nec.middleware.rbacAuth.rbac.util;

import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;

import java.math.BigDecimal;
import java.util.List;

/**
 * Static utility / helper methods shared across the RBAC module.
 *
 * <p>This class must NOT be instantiated – all methods are {@code static}.
 */
public final class RbacUtil {

    // Prevent instantiation
    private RbacUtil() {
        throw new UnsupportedOperationException("RbacUtil is a utility class");
    }

    // -------------------------------------------------------------------------
    // String helpers
    // -------------------------------------------------------------------------

    /**
     * Trims the given string and returns {@code null} if it is null or blank.
     *
     * @param value the string to normalise
     * @return trimmed value or {@code null}
     */
    public static String normalise(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    /**
     * Returns {@code true} if the string is not null and not blank.
     *
     * @param value the string to test
     * @return {@code true} if non-blank
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Capitalises the first letter of a word and lowercases the rest.
     *
     * @param word the input string
     * @return capitalised string, or {@code null} if input is null
     */
    public static String capitalise(String word) {
        if (word == null || word.isBlank()) {
            return word;
        }
        String trimmed = word.trim();
        return Character.toUpperCase(trimmed.charAt(0)) +
               trimmed.substring(1).toLowerCase();
    }

    // -------------------------------------------------------------------------
    // Validation helpers
    // -------------------------------------------------------------------------

    /**
     * Checks whether the given {@code status} string is one of the permitted values
     * ({@code "ACTIVE"} or {@code "INACTIVE"}).
     *
     * @param status the status value to validate
     * @return {@code true} if the status is valid
     */
    public static boolean isValidStatus(String status) {
        if (status == null) {
            return false;
        }
        return RbacConstants.STATUS_ACTIVE.equalsIgnoreCase(status)
            || RbacConstants.STATUS_INACTIVE.equalsIgnoreCase(status);
    }

    /**
     * Checks if a {@link Long} ID is valid (non-null and greater than 0).
     *
     * @param id the ID to validate
     * @return {@code true} if valid
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0L;
    }

    /**
     * Checks if a {@link BigDecimal} approval limit is valid (null or non-negative).
     *
     * @param approvalLimit the approval limit to validate
     * @return {@code true} if valid
     */
    public static boolean isValidApprovalLimit(BigDecimal approvalLimit) {
        if (approvalLimit == null) {
            return true;
        }
        return approvalLimit.compareTo(BigDecimal.ZERO) >= 0;
    }

    // -------------------------------------------------------------------------
    // Audit field conversion helpers
    // -------------------------------------------------------------------------

    /**
     * Converts a Long user ID to a String representation for audit fields.
     * Returns null if the input is null or invalid.
     *
     * @param userId the user ID to convert
     * @return the string representation of the user ID, or {@code null} if invalid
     */
    public static String toAuditUserId(Long userId) {
        return userId != null ? userId.toString() : null;
    }

    // -------------------------------------------------------------------------
    // Collection helpers
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if the list is non-null and not empty.
     *
     * @param list the list to test
     * @param <T>  the element type
     * @return {@code true} if non-empty
     */
    public static <T> boolean isNotEmpty(List<T> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * Returns an error message string combining a base message and a value.
     *
     * @param base  the base message (e.g., "Role not found with id: ")
     * @param value the dynamic value to append
     * @return the combined message string
     */
    public static String buildMessage(String base, Object value) {
        return base + value;
    }
}

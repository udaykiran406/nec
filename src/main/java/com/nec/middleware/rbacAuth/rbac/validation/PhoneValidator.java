package com.nec.middleware.rbacAuth.rbac.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the {@link Phone} annotation.
 *
 * <p>Validates phone numbers against the following rules:
 * <ul>
 *   <li>Not null or blank</li>
 *   <li>Minimum 7 digits (excluding formatting characters)</li>
 *   <li>Maximum 20 characters total</li>
 *   <li>Only digits, spaces, hyphens, and optional leading plus sign</li>
 * </ul>
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final int MIN_LENGTH = 7;
    private static final int MAX_LENGTH = 20;
    // Regex: starts with optional +, followed by digits, spaces, and hyphens
    private static final String PHONE_REGEX = "^\\+?[\\d\\s\\-]{" + (MIN_LENGTH - 1) + "," + (MAX_LENGTH - 1) + "}$";

    @Override
    public void initialize(Phone annotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are handled by @NotNull annotation
        if (value == null) {
            return true;
        }

        String trimmed = value.trim();

        // Check if blank
        if (trimmed.isEmpty()) {
            return false;
        }

        // Check length constraints (max 20 chars total)
        if (trimmed.length() > MAX_LENGTH) {
            return false;
        }

        // Count digits only (ignoring spaces, hyphens, plus)
        long digitCount = trimmed.replaceAll("[^\\d]", "").length();

        // Must have at least MIN_LENGTH digits
        if (digitCount < MIN_LENGTH) {
            return false;
        }

        // Validate format: optional leading +, then digits/spaces/hyphens
        return trimmed.matches(PHONE_REGEX);
    }
}


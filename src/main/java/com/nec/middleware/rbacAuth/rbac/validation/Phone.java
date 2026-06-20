package com.nec.middleware.rbacAuth.rbac.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for phone numbers.
 *
 * <p>Validates that the phone number:
 * <ul>
 *   <li>Is not null</li>
 *   <li>Is not blank</li>
 *   <li>Contains only digits, spaces, hyphens, and plus signs at the start</li>
 *   <li>Has minimum length of 7 characters (excluding spaces)</li>
 *   <li>Has maximum length of 20 characters</li>
 * </ul>
 *
 * <p>Examples of valid phone numbers:
 * <ul>
 *   <li>+255700000001</li>
 *   <li>+255 700 000 001</li>
 *   <li>255700000001</li>
 *   <li>0700000001</li>
 * </ul>
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface Phone {

    String message() default "Phone number must be a valid international or local format (7-20 characters, digits/hyphens/spaces/plus allowed)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


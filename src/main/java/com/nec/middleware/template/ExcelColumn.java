package com.nec.middleware.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a DTO field to be rendered as a column in an auto-generated
 * Excel import template (see ExcelTemplateService).
 *
 * Shared across modules (hr, rbac, etc.) — any DTO in any module can
 * use this annotation without creating a dependency on a specific
 * module's package.
 *
 * Usage example:
 *
 * @ExcelColumn(name = "Email", mandatory = true, sample = "john.doe@example.com")
 * @Email
 * @NotBlank
 * private String email;
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    /** Column header text shown in the Excel sheet. */
    String name();

    /** Whether this column is required. Drives header styling + comment text. */
    boolean mandatory() default false;

    /** Sample value written into the example row to guide the uploader. */
    String sample() default "";

    /** Extra free-text hint appended to the cell comment (e.g. format notes). */
    String description() default "";

    /** If non-empty, a dropdown (data validation list) is added for this column. */
    String[] allowedValues() default {};
}
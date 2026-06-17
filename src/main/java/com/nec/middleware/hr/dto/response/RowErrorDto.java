package com.nec.middleware.hr.dto.response;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Describes a single row-level failure during a bulk upload.
 *
 * <pre>
 * {
 *   "rowNumber": 4,
 *   "field":     "email",
 *   "message":   "A trainee with the same email or phone already exists"
 * }
 * </pre>
 *
 * {@code field} is optional — set it when the error is attributable to a
 * specific column (e.g. duplicate email).  Leave it {@code null} for
 * row-level failures (e.g. FK not found, unexpected exception).
 *
 * <p>{@code rawData} retains the original column values for this row
 * (header label → raw string value, in file column order) so that an error
 * report file can be regenerated with the user's original data plus the
 * failure reason appended as a trailing column. For rows that failed
 * during parsing, this map may be partially populated (only the columns
 * that were successfully read before the error occurred).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RowErrorDto {

    /** 1-based row number in the uploaded file (header = row 1, first data row = row 2). */
    private int rowNumber;

    /** Column/field name that caused the error, or {@code null} for row-level errors. */
    private String field;

    /** Human-readable reason for the failure. */
    private String message;

    /**
     * Original column values for this row, header label → raw string value,
     * in file column order. Used to rebuild the failed row in the error report.
     * Builder default ensures this is never null even if not explicitly set.
     */
    @Builder.Default
    private Map<String, String> rawData = new LinkedHashMap<>();
}

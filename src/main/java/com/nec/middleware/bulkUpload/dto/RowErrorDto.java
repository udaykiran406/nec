package com.nec.middleware.bulkUpload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Describes a single row-level failure during a bulk upload.
 *
 * <p>Identical in shape to the original {@code hr.dto.response.RowErrorDto}
 * — moved here so it's shared by every module (not just HR) without any
 * module depending on another module's package.
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
 * specific column. Leave it {@code null} for row-level failures (FK not
 * found, unexpected exception, structural parse error).
 *
 * <p>{@code rawData} retains the original column values for this row
 * (header label → raw string value) so the error report file can be
 * regenerated with the user's original input plus the failure reason
 * appended as a trailing column.
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
     * Original column values for this row, header label → raw string value.
     * Builder default ensures this is never null even if not explicitly set.
     */
    @Builder.Default
    private Map<String, String> rawData = new LinkedHashMap<>();
}

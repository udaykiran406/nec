package com.nec.middleware.bulkUpload.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Generic result envelope returned from the single
 * {@code POST /bulk-upload/{module}} endpoint, regardless of which module
 * was processed.
 *
 * <pre>
 * {
 *   "totalRows":     10,
 *   "successCount":  8,
 *   "failureCount":  2,
 *   "successRecords": [ ... ],
 *   "errors": [
 *     { "rowNumber": 3, "field": "email",  "message": "Already exists" },
 *     { "rowNumber": 7, "field": null,     "message": "Gender not found" }
 *   ]
 * }
 * </pre>
 *
 * @param <R> the module's response DTO type
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BulkUploadResultDto<R> {

    private int totalRows;
    private int successCount;
    private int failureCount;


    /**
     * Only present when failureCount > 0.
     * Open this URL in the browser — it will immediately download the .xlsx error report.
     * Valid for 30 minutes, single-use.
     *
     * Example: "http://localhost:8080/api/v1/bulk-upload/errors/download/a1b2c3d4-..."
     */
    private String errorDownloadUrl;

@JsonIgnore
    /** One entry per failed row, describing what went wrong and which row. */
    private List<RowErrorDto> errors;
}

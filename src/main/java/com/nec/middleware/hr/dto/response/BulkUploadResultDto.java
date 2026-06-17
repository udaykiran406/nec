package com.nec.middleware.hr.dto.response;

import lombok.*;

import java.util.List;

/**
 * Generic result envelope returned from every bulk-upload endpoint.
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
 * {@code T} is the entity-specific ResponseDto (e.g. {@code UniversityTraineeResponseDto}).
 * Using a generic here means {@code BulkUploadResultDto} is reused unchanged for
 * PortalUser, PoliticalPartyAgent, and MoI bulk uploads.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUploadResultDto<T> {

    private int totalRows;
    private int successCount;
    private int failureCount;

    /** Records that were saved successfully — each is the full ResponseDto of the created entity. */
    private List<T> successRecords;

    /** One entry per failed row, describing what went wrong and which row. */
    private List<RowErrorDto> errors;
}

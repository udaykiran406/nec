package com.nec.middleware.masterdata.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO returned by all Region (nec_regions) API endpoints.
 *
 * <p>Sample JSON:
 * <pre>
 * {
 *   "id": 1,
 *   "regionName": "Somaliland North",
 *   "status": "active",
 *   "createdBy": 1,
 *   "updatedBy": null,
 *   "createdAt": "2026-06-05T10:30:00",
 *   "updatedAt": "2026-06-05T10:30:00",
 *   "deletedAt": null,
 *   "isDeleted": 0
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterDataResponse {

    /** Primary key (S.NO in UI). */
    private Long id;

    /** Unique region name. */
    private String regionName;

    /** Status: 'active' or 'inactive'. */
    private String status;

    /** ID of the user who created this record. */
    private Long createdBy;

    /** ID of the user who last updated this record. */
    private Long updatedBy;

    /** Timestamp when the record was created. */
    private LocalDateTime createdAt;

    /** Timestamp when the record was last updated. */
    private LocalDateTime updatedAt;

    /** Soft-delete timestamp; null means not deleted. */
    private LocalDateTime deletedAt;

    /** Soft-delete flag: 0 = active, 1 = deleted. */
    private Short isDeleted;
}


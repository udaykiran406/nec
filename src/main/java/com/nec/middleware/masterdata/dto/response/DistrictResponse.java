package com.nec.middleware.masterdata.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO returned by all District (nec_districts) API endpoints.
 *
 * <p>Sample JSON:
 * <pre>
 * {
 *   "id": 1,
 *   "regionId": 1,
 *   "districtName": "Mogadishu",
 *   "status": "Active",
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
public class DistrictResponse {

    /** Primary key (S.NO in UI). */
    private Long id;

    /** Foreign key to region. */
    private Long regionId;

    /** Unique district name (within region). */
    private String districtName;

    /** Status: 'Active' or 'Inactive'. */
    private String status;


    private String createdBy;


    private String updatedBy;

    /** Timestamp when the record was created. */
    private LocalDateTime createdAt;

    /** Timestamp when the record was last updated. */
    private LocalDateTime updatedAt;

    /** Soft-delete timestamp; null means not deleted. */
    private LocalDateTime deletedAt;

    /** Soft-delete flag: 0 = active, 1 = deleted. */
    private Short isDeleted;
}



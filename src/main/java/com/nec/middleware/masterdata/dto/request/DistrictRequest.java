package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO used for both CREATE and UPDATE operations on {@code nec_districts}.
 *
 * <p>When {@code id} is {@code null} → INSERT; when non-null → UPDATE.
 *
 * <p>Sample JSON (create):
 * <pre>
 * {
 *   "regionId": 1,
 *   "districtName": "Mogadishu",
 *   "status": "Active",
 *   "createdBy": 1
 * }
 * </pre>
 *
 * <p>Sample JSON (update):
 * <pre>
 * {
 *   "id": 5,
 *   "regionId": 1,
 *   "districtName": "Mogadishu (Updated)",
 *   "status": "Active",
 *   "updatedBy": 2
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictRequest {

    /**
     * Optional. Provide when updating an existing district.
     */
    private Long id;

    /**
     * Foreign key to region (nec_regions.id).
     */
    @NotNull(message = "Region ID must not be null")
    @Min(value = 1, message = "Region ID must be a positive number")
    private Long regionId;

    /**
     * Unique name of the district within a region (1–150 characters, must not be blank).
     */
    @NotBlank(message = "District name must not be blank")
    @Size(min = 1, max = 150, message = "District name must be between 1 and 150 characters")
    private String districtName;

    /**
     * Status of the district: 'Active' or 'Inactive'.
     */
    @NotBlank(message = "Status must not be blank")
    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be 'Active' or 'Inactive'")
    private String status;



    private String createdBy;



    private String updatedBy;
}



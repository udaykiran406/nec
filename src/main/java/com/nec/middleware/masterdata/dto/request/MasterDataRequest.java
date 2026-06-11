package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO used for both CREATE and UPDATE operations on {@code nec_regions}.
 *
 * <p>When {@code id} is {@code null} → INSERT; when non-null → UPDATE.
 *
 * <p>Sample JSON (create):
 * <pre>
 * {
 *   "regionName": "Somaliland North",
 *   "status": "active",
 *   "createdBy": 1
 * }
 * </pre>
 *
 * <p>Sample JSON (update):
 * <pre>
 * {
 *   "id": 5,
 *   "regionName": "Somaliland North Revised",
 *   "status": "inactive",
 *   "updatedBy": 2
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterDataRequest {

    /**
     * Optional. Provide when updating an existing region.
     */
    private Long id;

    /**
     * Unique name of the region (1–150 characters, must not be blank).
     */
    @NotBlank(message = "Region name must not be blank")
    @Size(min = 1, max = 150, message = "Region name must be between 1 and 150 characters")
    private String regionName;

    /**
     * Status of the region: 'active' or 'inactive'.
     */
    @NotBlank(message = "Status must not be blank")
    @Pattern(regexp = "^(active|inactive)$", message = "Status must be 'active' or 'inactive'")
    private String status;

    /**
     * ID of the user creating the record (optional on update).
     */
    @Min(value = 1, message = "createdBy must be a positive number")
    private Long createdBy;

    /**
     * ID of the user updating the record (optional on create).
     */
    @Min(value = 1, message = "updatedBy must be a positive number")
    private Long updatedBy;
}


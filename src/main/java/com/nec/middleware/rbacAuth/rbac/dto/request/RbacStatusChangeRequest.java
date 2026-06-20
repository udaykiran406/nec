package com.nec.middleware.rbacAuth.rbac.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Request DTO used for changing the status of an {@code nec_rbac_roles} record.
 *
 * <p>Sample JSON:
 * <pre>
 * {
 *   "status": "INACTIVE",
 *   "modifiedByUserId": 2
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacStatusChangeRequest {

    /**
     * The new status to apply. Must be 'ACTIVE' or 'INACTIVE'.
     */
    @NotBlank(message = "Status is required.")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be 'ACTIVE' or 'INACTIVE'.")
    private String status;

    /**
     * ID of the user performing the status change (required).
     */
    @NotNull(message = "Modified By User ID is required.")
    @Min(value = 1, message = "Modified By User ID must be a positive number.")
    private Long modifiedByUserId;
}


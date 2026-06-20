package com.nec.middleware.rbacAuth.rbac.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Request DTO used for changing the status of an {@code nec_rbac_roles} record.
 *
 * <p>Sample JSON:
 * <pre>
 * {
 *   "status": "INACTIVE"
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacStatusChangeRequest {

    @NotBlank(message = "Status is required.")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be 'ACTIVE' or 'INACTIVE'.")
    private String status;
}

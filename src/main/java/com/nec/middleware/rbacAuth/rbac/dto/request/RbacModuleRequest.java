package com.nec.middleware.rbacAuth.rbac.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO used for both CREATE and UPDATE operations on {@code nec_rbac_module}.
 *
 * <p>When {@code moduleId} is {@code null} → INSERT; when non-null → UPDATE.
 *
 * <p>Sample JSON (create):
 * <pre>
 * {
 *   "moduleCode": "HR",
 *   "moduleName": "Human Resource",
 *   "displayOrder": 1,
 *   "status": "ACTIVE"
 * }
 * </pre>
 *
 * <p>Sample JSON (update):
 * <pre>
 * {
 *   "moduleId": 1,
 *   "moduleCode": "HR",
 *   "moduleName": "Human Resource",
 *   "displayOrder": 1,
 *   "status": "ACTIVE"
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacModuleRequest {

    /**
     * Optional. Provide when updating an existing module.
     */
    private Long moduleId;

    /**
     * Unique module code (1–50 characters, must not be blank).
     */
    @NotBlank(message = "Module Code is required.")
    @Size(min = 1, max = 50, message = "Module Code must be between 1 and 50 characters.")
    private String moduleCode;

    /**
     * Module name (1–100 characters, must not be blank).
     */
    @NotBlank(message = "Module Name is required.")
    @Size(min = 1, max = 100, message = "Module Name must be between 1 and 100 characters.")
    private String moduleName;

    /**
     * Display order for UI presentation (must be non-negative).
     */
    @NotNull(message = "Display Order is required.")
    @Min(value = 0, message = "Display Order must be a non-negative integer.")
    private Integer displayOrder;

    /**
     * Status of the module: 'ACTIVE' or 'INACTIVE'.
     */
    @NotBlank(message = "Status is required.")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be 'ACTIVE' or 'INACTIVE'.")
    private String status;
}



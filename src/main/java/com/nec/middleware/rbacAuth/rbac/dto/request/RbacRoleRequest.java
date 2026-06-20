package com.nec.middleware.rbacAuth.rbac.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Request DTO used for both CREATE and UPDATE operations on {@code nec_rbac_roles}.
 *
 * <p>When {@code roleId} is {@code null} → INSERT; when non-null → UPDATE.
 *
 * <p>Sample JSON (create) – {@code roleCode} is auto-generated; do NOT include it:
 * <pre>
 * {
 *   "roleName": "Administrator",
 *   "description": "Administrator role with full access",
 *   "status": "ACTIVE",
 *   "isParentRole": true
 * }
 * </pre>
 *
 * <p>Sample JSON (update):
 * <pre>
 * {
 *   "roleId": 1,
 *   "roleCode": "ADMIN",
 *   "roleName": "Administrator",
 *   "description": "Administrator role with full access",
 *   "status": "ACTIVE",
 *   "isParentRole": true,
 *   "approvalLimit": 100000.00
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacRoleRequest {

    /**
     * Optional. Provide when updating an existing role.
     */
    private Long roleId;

    /**
     * Role code – auto-generated on create; optional on update (not sent by client).
     * Format: &lt;PREFIX&gt;&lt;3-digit-sequence&gt;, e.g. ADMIN001.
     */
    @Size(max = 50, message = "Role Code must not exceed 50 characters.")
    private String roleCode;

    /**
     * Unique role name (1–100 characters, must not be blank).
     */
    @NotBlank(message = "Role Name is required.")
    @Size(min = 1, max = 100, message = "Role Name must be between 1 and 100 characters.")
    private String roleName;

    /**
     * Optional description of the role (max 500 characters).
     */
    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    /**
     * Optional parent role ID for hierarchical roles.
     */
    @Min(value = 1, message = "Parent Role ID must be a positive number.")
    private Long parentRoleId;

    /**
     * Optional approval limit. Must be null or non-negative.
     */
    @DecimalMin(value = "0", inclusive = true, message = "Approval Limit must be non-negative.")
    private BigDecimal approvalLimit;

    /**
     * Flag indicating whether this role is a parent role.
     */
    private Boolean isParentRole;

    /**
     * Status of the role: 'ACTIVE' or 'INACTIVE'.
     */
    @NotBlank(message = "Status is required.")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be 'ACTIVE' or 'INACTIVE'.")
    private String status;
}



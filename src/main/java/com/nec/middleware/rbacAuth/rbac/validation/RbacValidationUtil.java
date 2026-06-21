package com.nec.middleware.rbacAuth.rbac.validation;

import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacRoleRequest;
import com.nec.middleware.rbacAuth.rbac.util.RbacUtil;
import org.springframework.stereotype.Component;

/**
 * Business-level validation utility for the RBAC Roles module.
 *
 * <p>Jakarta Bean Validation handles field-level constraints (e.g. @NotBlank).
 * This class handles cross-field and business-rule validations that go beyond
 * simple annotation-based checks.
 */
@Component
public class  RbacValidationUtil {

    /**
     * Validates a create request.
     *
     * <ul>
     *   <li>Ensures the request itself is not null.</li>
     *   <li>Ensures {@code roleCode} is present and not blank.</li>
     *   <li>Ensures {@code roleName} is present and not blank.</li>
     *   <li>Ensures {@code status} is a permitted value.</li>
     *   <li>Ensures {@code approvalLimit} is valid (null or non-negative).</li>
     * </ul>
     *
     * @param request the create request to validate
     * @throws RbacRoleValidationException if any business rule is violated
     */
    public void validateCreateRequest(RbacRoleRequest request) {
        if (request == null) {
            throw new ValidationException(RbacConstants.REQUEST_NULL);
        }
        // roleCode is auto-generated; no client-side validation required here
        validateRoleName(request.getRoleName());
        validateStatus(request.getStatus());
        validateApprovalLimit(request.getApprovalLimit());
        validateParentRole(request.getParentRoleId(), null);
    }

    /**
     * Validates an update request.
     *
     * <ul>
     *   <li>Ensures the request itself is not null.</li>
     *   <li>Ensures {@code roleId} is present and positive.</li>
     *   <li>Validates role code, role name, status, approval limit if provided.</li>
     *   <li>Validates that a role cannot be its own parent.</li>
     * </ul>
     *
     * @param request the update request to validate
     * @throws RbacRoleValidationException if any business rule is violated
     */
    public void validateUpdateRequest(RbacRoleRequest request) {
        if (request == null) {
            throw new ValidationException(RbacConstants.REQUEST_NULL);
        }

        if (!RbacUtil.isValidId(request.getRoleId())) {
            throw new ValidationException(
                    "ID is required and must be a positive number for update");
        }

        if (RbacUtil.isNotBlank(request.getRoleCode())) {
            validateRoleCode(request.getRoleCode());
        }

        if (RbacUtil.isNotBlank(request.getRoleName())) {
            validateRoleName(request.getRoleName());
        }

        if (RbacUtil.isNotBlank(request.getStatus())) {
            validateStatus(request.getStatus());
        }

        if (request.getApprovalLimit() != null) {
            validateApprovalLimit(request.getApprovalLimit());
        }

        validateParentRole(request.getParentRoleId(), request.getRoleId());
    }
    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Validates that the role code is non-blank and within the allowed length.
     *
     * @param roleCode the code to validate
     * @throws RbacRoleValidationException if invalid
     */
    private void validateRoleCode(String roleCode) {
        if (!RbacUtil.isNotBlank(roleCode)) {
            throw new ValidationException(RbacConstants.INVALID_ROLE_CODE);
        }
        if (roleCode.trim().length() > 50) {
            throw new ValidationException(RbacConstants.INVALID_ROLE_CODE);
        }
    }

    /**
     * Validates that the role name is non-blank and within the allowed length.
     *
     * @param roleName the name to validate
     * @throws RbacRoleValidationException if invalid
     */
    private void validateRoleName(String roleName) {
        if (!RbacUtil.isNotBlank(roleName)) {
            throw new ValidationException(RbacConstants.INVALID_ROLE_NAME);
        }
        if (roleName.trim().length() > 100) {
            throw new ValidationException(RbacConstants.INVALID_ROLE_NAME);
        }
    }

    /**
     * Validates that the status is one of the permitted values.
     *
     * @param status the status to validate
     * @throws RbacRoleValidationException if invalid
     */
    private void validateStatus(String status) {
        if (!RbacUtil.isValidStatus(status)) {
            throw new ValidationException(RbacConstants.INVALID_STATUS);
        }
    }

    /**
     * Validates that the approval limit is valid (null or non-negative).
     *
     * @param approvalLimit the approval limit to validate
     * @throws RbacRoleValidationException if invalid
     */
    private void validateApprovalLimit(java.math.BigDecimal approvalLimit) {
        if (!RbacUtil.isValidApprovalLimit(approvalLimit)) {
            throw new ValidationException(RbacConstants.INVALID_APPROVAL_LIMIT);
        }
    }

    /**
     * Validates that a role doesn't reference itself as a parent.
     *
     * @param parentRoleId the parent role ID
     * @param currentRoleId the current role ID (for update validation)
     * @throws RbacRoleValidationException if the role references itself
     */
    private void validateParentRole(Long parentRoleId, Long currentRoleId) {
        if (parentRoleId != null && currentRoleId != null &&
            parentRoleId.equals(currentRoleId)) {
            throw new ValidationException(RbacConstants.INVALID_PARENT_ROLE);
        }
    }
}


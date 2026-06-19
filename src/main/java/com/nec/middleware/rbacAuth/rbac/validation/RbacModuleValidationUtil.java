package com.nec.middleware.rbacAuth.rbac.validation;

import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacModuleRequest;
import com.nec.middleware.rbacAuth.rbac.util.RbacUtil;
import org.springframework.stereotype.Component;

/**
 * Business-level validation utility for the RBAC Module module.
 *
 * <p>Jakarta Bean Validation handles field-level constraints (e.g. @NotBlank).
 * This class handles cross-field and business-rule validations that go beyond
 * simple annotation-based checks.
 */
@Component
public class RbacModuleValidationUtil {

    /**
     * Validates a create request.
     *
     * <ul>
     *   <li>Ensures the request itself is not null.</li>
     *   <li>Ensures {@code moduleCode} is present and not blank.</li>
     *   <li>Ensures {@code moduleName} is present and not blank.</li>
     *   <li>Ensures {@code displayOrder} is valid (non-negative).</li>
     *   <li>Ensures {@code status} is a permitted value.</li>
     * </ul>
     *
     * @param request the create request to validate
     * @throws ValidationException if any business rule is violated
     */
    public void validateCreateRequest(RbacModuleRequest request) {
        if (request == null) {
            throw new ValidationException(RbacConstants.REQUEST_NULL);
        }
        validateModuleCode(request.getModuleCode());
        validateModuleName(request.getModuleName());
        validateDisplayOrder(request.getDisplayOrder());
        validateStatus(request.getStatus());
    }

    /**
     * Validates an update request.
     *
     * <ul>
     *   <li>Ensures the request itself is not null.</li>
     *   <li>Ensures {@code moduleId} is present and positive.</li>
     *   <li>Validates module code, module name, display order, and status if provided.</li>
     * </ul>
     *
     * @param request the update request to validate
     * @throws ValidationException if any business rule is violated
     */
    public void validateUpdateRequest(RbacModuleRequest request) {
        if (request == null) {
            throw new ValidationException(RbacConstants.REQUEST_NULL);
        }
        if (!RbacUtil.isValidId(request.getModuleId())) {
            throw new ValidationException(
                    "ID is required and must be a positive number for update");
        }
        validateModuleCode(request.getModuleCode());
        validateModuleName(request.getModuleName());
        if (request.getDisplayOrder() != null) {
            validateDisplayOrder(request.getDisplayOrder());
        }
        if (RbacUtil.isNotBlank(request.getStatus())) {
            validateStatus(request.getStatus());
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Validates that the module code is non-blank and within the allowed length.
     *
     * @param moduleCode the code to validate
     * @throws ValidationException if invalid
     */
    private void validateModuleCode(String moduleCode) {
        if (!RbacUtil.isNotBlank(moduleCode)) {
            throw new ValidationException(RbacConstants.INVALID_MODULE_CODE);
        }
        if (moduleCode.trim().length() > 50) {
            throw new ValidationException(RbacConstants.INVALID_MODULE_CODE);
        }
    }

    /**
     * Validates that the module name is non-blank and within the allowed length.
     *
     * @param moduleName the name to validate
     * @throws ValidationException if invalid
     */
    private void validateModuleName(String moduleName) {
        if (!RbacUtil.isNotBlank(moduleName)) {
            throw new ValidationException(RbacConstants.INVALID_MODULE_NAME);
        }
        if (moduleName.trim().length() > 100) {
            throw new ValidationException(RbacConstants.INVALID_MODULE_NAME);
        }
    }

    /**
     * Validates that the display order is valid (non-negative).
     *
     * @param displayOrder the display order to validate
     * @throws ValidationException if invalid
     */
    private void validateDisplayOrder(Integer displayOrder) {
        if (displayOrder == null || displayOrder < 0) {
            throw new ValidationException(RbacConstants.INVALID_DISPLAY_ORDER);
        }
    }

    /**
     * Validates that the status is one of the permitted values.
     *
     * @param status the status to validate
     * @throws ValidationException if invalid
     */
    private void validateStatus(String status) {
        if (!RbacUtil.isValidStatus(status)) {
            throw new ValidationException(RbacConstants.INVALID_STATUS);
        }
    }
}



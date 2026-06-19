package com.nec.middleware.rbacAuth.rbac.mapper;

import com.nec.middleware.rbacAuth.rbac.dto.request.RbacModuleRequest;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacModuleResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacModule;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between {@link RbacModule} entity
 * and its request/response DTOs.
 *
 * <p>No MapStruct or any code-generation framework is used here –
 * all mapping logic is written explicitly for full transparency.
 */
@Component
public class RbacModuleMapper {

    /**
     * Converts a {@link RbacModuleRequest} to a new {@link RbacModule} entity
     * ready for INSERT. The {@code moduleId}, {@code createdDate}, {@code modifiedDate}
     * and {@code isDeleted} fields are managed by JPA / DB defaults.
     *
     * @param request the validated request DTO
     * @return a new entity (not yet persisted)
     */
    public RbacModule toEntity(RbacModuleRequest request) {
        return RbacModule.builder()
                .moduleCode(trimSafe(request.getModuleCode()))
                .moduleName(trimSafe(request.getModuleName()))
                .displayOrder(request.getDisplayOrder())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .createdByUserId(request.getCreatedByUserId())
                .modifiedByUserId(request.getModifiedByUserId())
                .build();
    }

    /**
     * Converts a persisted {@link RbacModule} entity to a {@link RbacModuleResponse} DTO.
     *
     * @param entity the persisted entity
     * @return the response DTO
     */
    public RbacModuleResponse toResponseDto(RbacModule entity) {
        return RbacModuleResponse.builder()
                .moduleCode(entity.getModuleCode())
                .moduleName(entity.getModuleName())
                .displayOrder(entity.getDisplayOrder())
                .status(entity.getStatus())
                .build();
    }

    /**
     * Applies updatable fields from the request DTO onto an existing entity (for UPDATE).
     * Fields that are {@code null} in the request are left unchanged on the entity.
     *
     * @param entity the existing entity to be updated
     * @param request the request DTO carrying new values
     */
    public void updateEntity(RbacModule entity, RbacModuleRequest request) {
        if (request.getModuleCode() != null && !request.getModuleCode().isBlank()) {
            entity.setModuleCode(trimSafe(request.getModuleCode()));
        }
        if (request.getModuleName() != null && !request.getModuleName().isBlank()) {
            entity.setModuleName(trimSafe(request.getModuleName()));
        }
        if (request.getDisplayOrder() != null) {
            entity.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            entity.setStatus(request.getStatus());
        }
        if (request.getModifiedByUserId() != null) {
            entity.setModifiedByUserId(request.getModifiedByUserId());
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Trims a string safely; returns {@code null} if the input is {@code null}.
     *
     * @param value the string to trim
     * @return trimmed string or {@code null}
     */
    private String trimSafe(String value) {
        return value != null ? value.trim() : null;
    }
}



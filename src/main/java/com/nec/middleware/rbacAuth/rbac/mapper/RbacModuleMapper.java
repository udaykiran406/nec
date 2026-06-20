package com.nec.middleware.rbacAuth.rbac.mapper;

import com.nec.middleware.rbacAuth.rbac.dto.request.RbacModuleRequest;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacModuleResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacModule;
import org.springframework.stereotype.Component;

@Component
public class RbacModuleMapper {

    public RbacModule toEntity(RbacModuleRequest request) {
        return RbacModule.builder()
                .moduleCode(trimSafe(request.getModuleCode()))
                .moduleName(trimSafe(request.getModuleName()))
                .displayOrder(request.getDisplayOrder())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();
    }

    public RbacModuleResponse toResponseDto(RbacModule entity) {
        return RbacModuleResponse.builder()
                .moduleCode(entity.getModuleCode())
                .moduleName(entity.getModuleName())
                .displayOrder(entity.getDisplayOrder())
                .status(entity.getStatus())
                .build();
    }

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
    }

    private String trimSafe(String value) {
        return value != null ? value.trim() : null;
    }
}

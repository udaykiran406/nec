package com.nec.middleware.rbacAuth.rbac.mapper;

import com.nec.middleware.rbacAuth.rbac.dto.request.RbacRoleRequest;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRoleResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacRole;
import com.nec.middleware.rbacAuth.rbac.util.RbacUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RbacRoleMapper {

    private final AssociationMapper associationMapper;

    public RbacRole toEntity(RbacRoleRequest request) {
        return RbacRole.builder()
                .roleCode(trimSafe(request.getRoleCode()))
                .roleName(trimSafe(request.getRoleName()))
                .description(trimSafe(request.getDescription()))
                .parentRoleId(request.getParentRoleId())
                .approvalLimit(request.getApprovalLimit())
                .isParentRole(request.getIsParentRole() != null ? request.getIsParentRole() : false)
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .createdBy(request.getCreatedByUserId() != null ? String.valueOf(request.getCreatedByUserId()) : null)
                .updatedBy(RbacUtil.toAuditUserId(request.getModifiedByUserId()))
                .build();
    }

    public RbacRoleResponse toResponseDto(RbacRole entity) {
        return RbacRoleResponse.builder()
                .roleCode(entity.getRoleCode())
                .roleName(entity.getRoleName())
                .description(entity.getDescription())
                .parentRole(associationMapper.toRole(entity.getParentRole()))
                .approvalLimit(entity.getApprovalLimit())
                .isParentRole(entity.getIsParentRole())
                .status(entity.getStatus())
                .build();
    }

    public void updateEntity(RbacRole entity, RbacRoleRequest request) {
        if (request.getRoleCode() != null && !request.getRoleCode().isBlank()) {
            entity.setRoleCode(trimSafe(request.getRoleCode()));
        }
        if (request.getRoleName() != null && !request.getRoleName().isBlank()) {
            entity.setRoleName(trimSafe(request.getRoleName()));
        }
        if (request.getDescription() != null) {
            entity.setDescription(trimSafe(request.getDescription()));
        }
        if (request.getParentRoleId() != null) {
            entity.setParentRoleId(request.getParentRoleId());
        }
        if (request.getApprovalLimit() != null) {
            entity.setApprovalLimit(request.getApprovalLimit());
        }
        if (request.getIsParentRole() != null) {
            entity.setIsParentRole(request.getIsParentRole());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            entity.setStatus(request.getStatus());
        }
        if (request.getModifiedByUserId() != null) {
            entity.setUpdatedBy(RbacUtil.toAuditUserId(request.getModifiedByUserId()));
        }
    }

    private String trimSafe(String value) {
        return value != null ? value.trim() : null;
    }
}

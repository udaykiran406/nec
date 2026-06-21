package com.nec.middleware.rbacAuth.rbac.mapper;

import com.nec.middleware.rbacAuth.rbac.dto.request.RbacPermissionGroupRequest;
import com.nec.middleware.rbacAuth.rbac.entity.RbacModule;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermission;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermissionGroup;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting RBAC permission group DTOs to entities.
 * Provides manual mapping logic for full transparency.
 */
@Component
public class RbacHierarchyMapper {

    /**
     * Converts a ModuleRequest DTO to RbacModule entity.
     *
     * @param request the module request DTO
     * @return the RbacModule entity
     */
    public RbacModule toModuleEntity(RbacPermissionGroupRequest.ModuleRequest request) {
        return RbacModule.builder()
                .moduleId(request.getModuleId())
                .moduleCode(trimSafe(request.getModuleCode()))
                .moduleName(trimSafe(request.getModuleName()))
                .description(trimSafe(request.getDescription()))
                .displayOrder(request.getDisplayOrder())
                .status(request.getStatus())
                .createdByUserId(request.getCreatedByUserId())
                .modifiedByUserId(request.getModifiedByUserId())
                .build();
    }

    /**
     * Converts a GroupRequest DTO to RbacPermissionGroup entity.
     *
     * @param request the group request DTO
     * @param moduleId the module ID to associate
     * @return the RbacPermissionGroup entity
     */
    public RbacPermissionGroup toGroupEntity(RbacPermissionGroupRequest.GroupRequest request, Long moduleId) {
        return RbacPermissionGroup.builder()
                .groupId(request.getGroupId())
                .moduleId(moduleId)
                .groupCode(trimSafe(request.getGroupCode()))
                .groupName(trimSafe(request.getGroupName()))
                .description(trimSafe(request.getDescription()))
                .displayOrder(request.getDisplayOrder())
                .status(request.getStatus())
                .createdByUserId(request.getCreatedByUserId())
                .modifiedByUserId(request.getModifiedByUserId())
                .build();
    }

    /**
     * Converts a PermissionRequest DTO to RbacPermission entity.
     *
     * @param request the permission request DTO
     * @param moduleId the module ID to associate
     * @param groupId the group ID to associate
     * @return the RbacPermission entity
     */
    public RbacPermission toPermissionEntity(RbacPermissionGroupRequest.PermissionRequest request, Long moduleId, Long groupId) {
        return RbacPermission.builder()
                .permissionId(request.getPermissionId())
                .moduleId(moduleId)
                .groupId(groupId)
                .permissionCode(trimSafe(request.getPermissionCode()))
                .permissionName(trimSafe(request.getPermissionName()))
                .description(trimSafe(request.getDescription()))
                .displayOrder(request.getDisplayOrder())
                .status(request.getStatus())
                .isSideMenu(request.getIsSideMenu() != null ? request.getIsSideMenu() : false)
                .createdByUserId(request.getCreatedByUserId())
                .modifiedByUserId(request.getModifiedByUserId())
                .build();
    }

    /**
     * Safely trims a string; returns null if input is null.
     *
     * @param value the string to trim
     * @return trimmed string or null
     */
    private String trimSafe(String value) {
        return value != null ? value.trim() : null;
    }
}



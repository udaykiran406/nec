package com.nec.middleware.rbacAuth.rbac.mapper;

import com.nec.middleware.rbacAuth.rbac.dto.request.RbacPermissionGroupRequest;
import com.nec.middleware.rbacAuth.rbac.entity.RbacModule;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermission;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermissionGroup;
import org.springframework.stereotype.Component;

@Component
public class RbacHierarchyMapper {

    public RbacModule toModuleEntity(RbacPermissionGroupRequest.ModuleRequest request) {
        return RbacModule.builder()
                .moduleId(request.getModuleId())
                .moduleCode(trimSafe(request.getModuleCode()))
                .moduleName(trimSafe(request.getModuleName()))
                .description(trimSafe(request.getDescription()))
                .displayOrder(request.getDisplayOrder())
                .status(request.getStatus())
                .build();
    }

    public RbacPermissionGroup toGroupEntity(RbacPermissionGroupRequest.GroupRequest request, Long moduleId) {
        return RbacPermissionGroup.builder()
                .groupId(request.getGroupId())
                .moduleId(moduleId)
                .groupCode(trimSafe(request.getGroupCode()))
                .groupName(trimSafe(request.getGroupName()))
                .description(trimSafe(request.getDescription()))
                .displayOrder(request.getDisplayOrder())
                .status(request.getStatus())
                .build();
    }

    public RbacPermission toPermissionEntity(
            RbacPermissionGroupRequest.PermissionRequest request, Long moduleId, Long groupId) {
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
                .build();
    }

    private String trimSafe(String value) {
        return value != null ? value.trim() : null;
    }
}

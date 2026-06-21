package com.nec.middleware.rbacAuth.rbac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * Request DTO for Role Permission Mapping.
 *
 * <p>Contains the role name and the complete hierarchy:
 * Role -> Module -> Group -> Permission
 *
 * <p>For UPDATE operations, provide rolePermissionId. For INSERT, leave it null.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacRolePermissionRequest {

    /**
     * The role permission mapping ID (for UPDATE operations).
     * Leave null for INSERT operations.
     */
    @Min(value = 1, message = "Role Permission ID must be a positive number.")
    private Long rolePermissionId;

    /**
     * The ID of the role to which permissions are being mapped.
     */
    @NotNull(message = "Role ID is missing.")
    @Min(value = 1, message = "Role ID must be a positive number.")
    private Long roleId;

    /**
     * The name of the role (optional, for display purposes only).
     */
    private String roleName;

    /**
     * List of modules with their groups and permissions.
     */
    @NotEmpty(message = "Module list is missing.")
    @Valid
    private List<ModuleRequestDto> modules;

    /**
     * Nested DTO for Module information.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModuleRequestDto {

        /**
         * Module ID (required to identify the module).
         */
        @NotNull(message = "Module Id is missing.")
        @Min(value = 1, message = "Module Id must be a positive number.")
        private Long moduleId;

        /**
         * Module name (for reference/validation).
         */
        private String moduleName;

        /**
         * List of permission groups within the module.
         */
        @NotEmpty(message = "Group list is missing.")
        @Valid
        private List<GroupRequestDto> groups;
    }

    /**
     * Nested DTO for Permission Group information.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupRequestDto {

        /**
         * Group ID (required to identify the group).
         */
        @NotNull(message = "Group Id is missing.")
        @Min(value = 1, message = "Group Id must be a positive number.")
        private Long groupId;

        /**
         * Group name (for reference/validation).
         */
        private String groupName;

        /**
         * List of permissions within the group.
         */
        @NotEmpty(message = "Permission list is missing.")
        @Valid
        private List<PermissionRequestDto> permissions;
    }

    /**
     * Nested DTO for Permission information.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PermissionRequestDto {

        /**
         * Permission ID (required to identify the permission).
         */
        @NotNull(message = "Permission Id is missing.")
        @Min(value = 1, message = "Permission Id must be a positive number.")
        private Long permissionId;

        /**
         * Permission name (for reference/validation).
         */
        private String permissionName;
    }
}



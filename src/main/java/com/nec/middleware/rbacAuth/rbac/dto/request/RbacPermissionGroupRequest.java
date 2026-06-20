package com.nec.middleware.rbacAuth.rbac.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * Request DTO for hierarchical permission groups and permissions data.
 * Represents the complete RBAC permission group hierarchy structure.
 *
 * <p>Sample JSON:
 * <pre>
 * {
 *   "modules": [
 *     {
 *       "moduleId": 1,
 *       "moduleCode": "HR",
 *       "moduleName": "Human Resource",
 *       "description": "HR Module",
 *       "displayOrder": 1,
 *       "status": "ACTIVE",
 *       "groups": [
 *         {
 *           "groupId": 101,
 *           "groupCode": "TPP",
 *           "groupName": "Third Party Portal",
 *           "description": "TPP Group",
 *           "displayOrder": 1,
 *           "status": "ACTIVE",
 *           "permissions": [
 *             {
 *               "permissionId": 10001,
 *               "permissionCode": "CREATE_USER",
 *               "permissionName": "Create User",
 *               "description": "Create new user",
 *               "displayOrder": 1,
 *               "status": "ACTIVE"
 *             }
 *           ]
 *         }
 *       ]
 *     }
 *   ]
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacPermissionGroupRequest {

    /**
     * List of modules with their permission groups and permissions.
     */
    @NotNull(message = "Modules list is required.")
    @NotEmpty(message = "At least one module must be provided.")
    @Valid
    private List<ModuleRequest> modules;

    /**
     * Module Request DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModuleRequest {

        /**
         * Module ID (null for new modules, value for updates).
         */
        private Long moduleId;

        /**
         * Unique module code (1-50 characters).
         */
        @NotBlank(message = "Module Code is required.")
        @Size(min = 1, max = 50, message = "Module Code must be between 1 and 50 characters.")
        private String moduleCode;

        /**
         * Module name (1-100 characters).
         */
        @NotBlank(message = "Module Name is required.")
        @Size(min = 1, max = 100, message = "Module Name must be between 1 and 100 characters.")
        private String moduleName;

        /**
         * Optional description (max 500 characters).
         */
        @Size(max = 500, message = "Description must not exceed 500 characters.")
        private String description;

        /**
         * Display order (non-negative).
         */
        @NotNull(message = "Display Order is required.")
        @Min(value = 0, message = "Display Order must be non-negative.")
        private Integer displayOrder;

        /**
         * Status (ACTIVE/INACTIVE).
         */
        @NotBlank(message = "Status is required.")
        @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be 'ACTIVE' or 'INACTIVE'.")
        private String status;

        /**
         * Permission groups within this module.
         */
        @NotNull(message = "Permission Groups list is required.")
        @NotEmpty(message = "At least one permission group must be provided.")
        @Valid
        private List<GroupRequest> groups;
    }

    /**
     * Permission Group Request DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupRequest {

        /**
         * Group ID (null for new groups, value for updates).
         */
        private Long groupId;

        /**
         * Unique group code within module (1-50 characters).
         */
        @NotBlank(message = "Group Code is required.")
        @Size(min = 1, max = 50, message = "Group Code must be between 1 and 50 characters.")
        private String groupCode;

        /**
         * Group name (1-100 characters).
         */
        @NotBlank(message = "Group Name is required.")
        @Size(min = 1, max = 100, message = "Group Name must be between 1 and 100 characters.")
        private String groupName;

        /**
         * Optional description (max 500 characters).
         */
        @Size(max = 500, message = "Description must not exceed 500 characters.")
        private String description;

        /**
         * Display order (non-negative).
         */
        @NotNull(message = "Display Order is required.")
        @Min(value = 0, message = "Display Order must be non-negative.")
        private Integer displayOrder;

        /**
         * Status (ACTIVE/INACTIVE).
         */
        @NotBlank(message = "Status is required.")
        @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be 'ACTIVE' or 'INACTIVE'.")
        private String status;

        /**
         * Permissions within this group.
         */
        @NotNull(message = "Permissions list is required.")
        @NotEmpty(message = "At least one permission must be provided.")
        @Valid
        private List<PermissionRequest> permissions;
    }

    /**
     * Permission Request DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PermissionRequest {

        /**
         * Permission ID (null for new permissions, value for updates).
         */
        private Long permissionId;

        /**
         * Unique permission code within module (1-50 characters).
         */
        @NotBlank(message = "Permission Code is required.")
        @Size(min = 1, max = 50, message = "Permission Code must be between 1 and 50 characters.")
        private String permissionCode;

        /**
         * Permission name (1-100 characters).
         */
        @NotBlank(message = "Permission Name is required.")
        @Size(min = 1, max = 100, message = "Permission Name must be between 1 and 100 characters.")
        private String permissionName;

        /**
         * Optional description (max 500 characters).
         */
        @Size(max = 500, message = "Description must not exceed 500 characters.")
        private String description;

        /**
         * Display order (non-negative).
         */
        @NotNull(message = "Display Order is required.")
        @Min(value = 0, message = "Display Order must be non-negative.")
        private Integer displayOrder;

        /**
         * Status (ACTIVE/INACTIVE).
         */
        @NotBlank(message = "Status is required.")
        @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be 'ACTIVE' or 'INACTIVE'.")
        private String status;

        /**
         * Flag to indicate if permission appears in side menu (default: false).
         */
        private Boolean isSideMenu;
    }
}



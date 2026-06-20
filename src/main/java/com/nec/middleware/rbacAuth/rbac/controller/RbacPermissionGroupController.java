package com.nec.middleware.rbacAuth.rbac.controller;

import com.nec.middleware.rbacAuth.rbac.constant.ApiMessageConstants;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.GroupListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.request.ModuleListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacPermissionGroupRequest;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacGroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacModuleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacPermissionGroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacPermissionGroupModuleResponse;
import com.nec.middleware.rbacAuth.rbac.service.PermissionGroupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rbac")
@AllArgsConstructor
public class RbacPermissionGroupController {

    private final PermissionGroupService permissionGroupService;

    // =========================================================================
    // PERMISSION GROUP
    // =========================================================================

    /**
     * Creates a new permission group hierarchy (modules, groups, permissions).
     * All IDs in the request should be null.
     */
    @PostMapping("/permission-groups")
    public ResponseEntity<ApiResponse<RbacPermissionGroupResponse>> createPermissionGroup(
            @Valid @RequestBody RbacPermissionGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(permissionGroupService.createPermissionGroup(request));
    }

    /**
     * Updates an existing permission group hierarchy (modules, groups, permissions).
     * All IDs in the request must be present.
     */
    @PutMapping("/permission-groups")
    public ResponseEntity<ApiResponse<RbacPermissionGroupResponse>> updatePermissionGroup(
            @Valid @RequestBody RbacPermissionGroupRequest request) {
        return ResponseEntity.ok(permissionGroupService.updatePermissionGroup(request));
    }

    /**
     * Retrieves all modules with their complete permission group hierarchies (groups and permissions).
     * Only returns non-deleted modules, groups, and permissions, ordered by module display order.
     */
    @GetMapping("/permission-group/all")
    public ResponseEntity<ApiResponse<RbacPermissionGroupResponse>> getAllPermissionGroups() {
        RbacPermissionGroupResponse response = permissionGroupService.getAllPermissionGroups();
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.PERMISSION_GROUP_FETCHED, response));
    }

    /**
     * Retrieves permission group hierarchy with optional pagination, sorting, search, and module-level filters.
     */
    @PostMapping("/permission-group/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<RbacPermissionGroupModuleResponse>>> listPermissionGroups(
            @Valid @RequestBody(required = false) ModuleListRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok(
                ApiMessageConstants.DATA_FETCHED,
                permissionGroupService.listPermissionGroups(request)));
    }

    /**
     * Retrieves a specific module with its complete permission group hierarchy (groups and permissions).
     *
     * @param moduleId the primary key of the module
     */
    @GetMapping("/permission-group/{moduleId}")
    public ResponseEntity<ApiResponse<RbacPermissionGroupResponse>> getPermissionGroupByModuleId(
            @PathVariable Long moduleId) {
        RbacPermissionGroupResponse response = permissionGroupService.getPermissionGroupByModuleId(moduleId);
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.PERMISSION_GROUP_FETCHED, response));
    }

    // =========================================================================
    // MODULE
    // =========================================================================

    /**
     * Retrieves modules with optional pagination, sorting, global search, and dynamic filters.
     */
    @PostMapping("/module/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<RbacModuleResponse>>> listModules(
            @Valid @RequestBody(required = false) ModuleListRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok(
                ApiMessageConstants.DATA_FETCHED,
                permissionGroupService.listModules(request)));
    }

    // =========================================================================
    // GROUP
    // =========================================================================

    /**
     * Retrieves permission groups with optional pagination, sorting, global search, and dynamic filters.
     */
    @PostMapping("/group/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<RbacGroupResponse>>> listGroups(
            @Valid @RequestBody(required = false) GroupListRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok(
                ApiMessageConstants.DATA_FETCHED,
                permissionGroupService.listGroups(request)));
    }

}

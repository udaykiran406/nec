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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
        int moduleCount = request.getModules() != null ? request.getModules().size() : 0;
        log.info("Create permission group request received: moduleCount={}", moduleCount);
        ResponseEntity<ApiResponse<RbacPermissionGroupResponse>> response =
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(permissionGroupService.createPermissionGroup(request));
        log.info("Permission group created successfully: moduleCount={}", moduleCount);
        return response;
    }

    /**
     * Updates an existing permission group hierarchy (modules, groups, permissions).
     * All IDs in the request must be present.
     */
    @PutMapping("/permission-groups")
    public ResponseEntity<ApiResponse<RbacPermissionGroupResponse>> updatePermissionGroup(
            @Valid @RequestBody RbacPermissionGroupRequest request) {
        int moduleCount = request.getModules() != null ? request.getModules().size() : 0;
        log.info("Update permission group request received: moduleCount={}", moduleCount);
        ResponseEntity<ApiResponse<RbacPermissionGroupResponse>> response =
                ResponseEntity.ok(permissionGroupService.updatePermissionGroup(request));
        log.info("Permission group updated successfully: moduleCount={}", moduleCount);
        return response;
    }

    /**
     * Retrieves all modules with their complete permission group hierarchies (groups and permissions).
     * Only returns non-deleted modules, groups, and permissions, ordered by module display order.
     */
    @GetMapping("/permission-group/all")
    public ResponseEntity<ApiResponse<RbacPermissionGroupResponse>> getAllPermissionGroups() {
        log.info("Fetching all permission groups");
        RbacPermissionGroupResponse response = permissionGroupService.getAllPermissionGroups();
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.PERMISSION_GROUP_FETCHED, response));
    }

    /**
     * Retrieves permission group hierarchy with optional pagination, sorting, search, and module-level filters.
     */
    @PostMapping("/permission-group/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<RbacPermissionGroupModuleResponse>>> listPermissionGroups(
            @Valid @RequestBody(required = false) ModuleListRequestDto request) {
        ModuleListRequestDto listRequest = request != null ? request : new ModuleListRequestDto();
        log.info("Fetching permission groups with filters: page={}, size={}, search={}",
                listRequest.getPage(), listRequest.getSize(), listRequest.getSearch());
        PaginatedResponse<RbacPermissionGroupModuleResponse> response =
                permissionGroupService.listPermissionGroups(request);
        return ResponseEntity.ok(ApiResponse.ok(
                ApiMessageConstants.DATA_FETCHED,
                response));
    }

    /**
     * Retrieves a specific module with its complete permission group hierarchy (groups and permissions).
     *
     * @param moduleId the primary key of the module
     */
    @GetMapping("/permission-group/{moduleId}")
    public ResponseEntity<ApiResponse<RbacPermissionGroupResponse>> getPermissionGroupByModuleId(
            @PathVariable Long moduleId) {
        log.info("Fetch permission group request received: moduleId={}", moduleId);
        RbacPermissionGroupResponse response = permissionGroupService.getPermissionGroupByModuleId(moduleId);
        log.debug("Permission group fetched successfully: moduleId={}", moduleId);
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
        ModuleListRequestDto listRequest = request != null ? request : new ModuleListRequestDto();
        log.info("Fetching RBAC modules with filters: page={}, size={}, search={}",
                listRequest.getPage(), listRequest.getSize(), listRequest.getSearch());
        PaginatedResponse<RbacModuleResponse> response = permissionGroupService.listModules(request);
        return ResponseEntity.ok(ApiResponse.ok(
                ApiMessageConstants.DATA_FETCHED,
                response));
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
        GroupListRequestDto listRequest = request != null ? request : new GroupListRequestDto();
        log.info("Fetching RBAC groups with filters: page={}, size={}, search={}",
                listRequest.getPage(), listRequest.getSize(), listRequest.getSearch());
        PaginatedResponse<RbacGroupResponse> response = permissionGroupService.listGroups(request);
        return ResponseEntity.ok(ApiResponse.ok(
                ApiMessageConstants.DATA_FETCHED,
                response));
    }

}

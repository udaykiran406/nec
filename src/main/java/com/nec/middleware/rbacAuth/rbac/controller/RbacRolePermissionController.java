package com.nec.middleware.rbacAuth.rbac.controller;

import com.nec.middleware.rbacAuth.rbac.constant.ApiMessageConstants;
import com.nec.middleware.rbacAuth.rbac.constant.RbacRolePermissionConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacRolePermissionRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.RolePermissionListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRolePermissionResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRolePermissionResponseDto;
import com.nec.middleware.rbacAuth.rbac.service.RolePermissionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/rbac/role-permission")
@AllArgsConstructor
public class RbacRolePermissionController {

    private final RolePermissionService rolePermissionService;

    /**
     * Creates new role-permission mappings. Fails if any mapping already exists.
     *
     * @param request the role permission request DTO
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RbacRolePermissionResponse>> createRolePermission(
            @Valid @RequestBody RbacRolePermissionRequest request) {
        int moduleCount = request.getModules() != null ? request.getModules().size() : 0;
        log.info("Create role-permission request received: roleId={}, moduleCount={}",
                request.getRoleId(), moduleCount);
        ResponseEntity<ApiResponse<RbacRolePermissionResponse>> response =
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(rolePermissionService.createRolePermission(request));
        log.info("Role-permission mappings created successfully: roleId={}", request.getRoleId());
        return response;
    }

    /**
     * Updates (activates or adds) existing role-permission mappings.
     *
     * @param request the role permission request DTO
     */
    @PutMapping
    public ResponseEntity<ApiResponse<RbacRolePermissionResponse>> updateRolePermission(
            @Valid @RequestBody RbacRolePermissionRequest request) {
        int moduleCount = request.getModules() != null ? request.getModules().size() : 0;
        log.info("Update role-permission request received: roleId={}, moduleCount={}",
                request.getRoleId(), moduleCount);
        ResponseEntity<ApiResponse<RbacRolePermissionResponse>> response =
                ResponseEntity.ok(rolePermissionService.updateRolePermission(request));
        log.info("Role-permission mappings updated successfully: roleId={}", request.getRoleId());
        return response;
    }

    /**
     * Retrieves all active role-permission mappings for a specific role in hierarchical format.
     * Data is grouped by Module → Group → Permissions.
     *
     * @param roleId the role ID for which to retrieve all permissions
     */
    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<RbacRolePermissionResponse>> getRolePermissionsByRoleId(
            @PathVariable @Min(value = 1, message = "roleId must be a positive number") Long roleId) {
        log.info("Fetch role-permission request received: roleId={}", roleId);
        RbacRolePermissionResponse response = rolePermissionService.getRolePermissionsByRoleId(roleId);
        log.debug("Role-permission hierarchy fetched successfully: roleId={}", roleId);
        return ResponseEntity.ok(ApiResponse.ok(RbacRolePermissionConstants.ROLE_PERMISSIONS_FETCHED, response));
    }

    /**
     * Retrieves all active role-permission mappings in hierarchical format.
     * Data is grouped by Role → Module → Group → Permissions.
     */
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<List<RbacRolePermissionResponse>>> getAllRolePermissions() {
        log.info("Fetching all role-permission mappings");
        List<RbacRolePermissionResponse> response = rolePermissionService.getAllRolePermissions();
        log.debug("Fetched {} role-permission hierarchies", response.size());
        return ResponseEntity.ok(ApiResponse.ok(RbacRolePermissionConstants.ROLE_PERMISSIONS_FETCHED, response));
    }

    /**
     * Retrieves role-permission mappings with optional pagination, sorting, and dynamic filters.
     */
    @PostMapping("/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<RbacRolePermissionResponseDto>>> listRolePermissions(
            @Valid @RequestBody(required = false) RolePermissionListRequestDto request) {
        RolePermissionListRequestDto listRequest = request != null ? request : new RolePermissionListRequestDto();
        log.info("Fetching role-permissions with filters: page={}, size={}, search={}",
                listRequest.getPage(), listRequest.getSize(), listRequest.getSearch());
        PaginatedResponse<RbacRolePermissionResponseDto> response =
                rolePermissionService.listRolePermissions(request);
        return ResponseEntity.ok(ApiResponse.ok(
                ApiMessageConstants.DATA_FETCHED,
                response));
    }

    /**
     * Soft-deletes a role-permission mapping by setting status to 'INACTIVE'.
     * Returns the remaining active permissions for the role in hierarchical format.
     *
     * @param rolePermissionId the ID of the mapping to delete
     */
    @DeleteMapping("/{rolePermissionId}")
    public ResponseEntity<ApiResponse<RbacRolePermissionResponse>> deleteRolePermission(
            @PathVariable @Min(value = 1, message = "rolePermissionId must be a positive number") Long rolePermissionId) {
        log.info("Soft deleting role-permission mapping: rolePermissionId={}", rolePermissionId);
        RbacRolePermissionResponse response = rolePermissionService.deleteRolePermission(rolePermissionId);
        log.info("Role-permission mapping soft-deleted successfully: rolePermissionId={}", rolePermissionId);
        return ResponseEntity.ok(ApiResponse.ok(RbacRolePermissionConstants.ROLE_PERMISSION_DELETED, response));
    }

}

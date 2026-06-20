package com.nec.middleware.rbacAuth.rbac.controller;

import com.nec.middleware.rbacAuth.rbac.constant.ApiMessageConstants;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacRoleRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacStatusChangeRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.RoleListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRoleResponse;
import com.nec.middleware.rbacAuth.rbac.service.RoleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/rbac")
@AllArgsConstructor
public class RbacRoleController {

    private final RoleService roleService;

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<RbacRoleResponse>> createRole(
            @Valid @RequestBody RbacRoleRequest request) {
        log.info("Create role request received: roleName={}", request.getRoleName());
        RbacRoleResponse response = roleService.createRole(request);
        log.info("Role created successfully: roleCode={}", response.getRoleCode());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(RbacConstants.ROLE_CREATED, response));
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<RbacRoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RbacRoleRequest request) {
        log.info("Update role request received: roleId={}, roleName={}", id, request.getRoleName());
        RbacRoleResponse response = roleService.updateRole(id, request);
        log.info("Role updated successfully: roleId={}, roleCode={}", id, response.getRoleCode());
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLE_UPDATED, response));
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<RbacRoleResponse>> getById(@PathVariable Long id) {
        log.info("Fetch role request received: roleId={}", id);
        RbacRoleResponse response = roleService.getRoleById(id);
        log.debug("Role fetched successfully: roleId={}", id);
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLE_FETCHED, response));
    }

    @GetMapping("/roles/all")
    public ResponseEntity<ApiResponse<List<RbacRoleResponse>>> getAll() {
        log.info("Fetching all RBAC roles");
        List<RbacRoleResponse> response = roleService.getAllRoles();
        log.debug("Fetched {} RBAC roles", response.size());
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLES_FETCHED, response));
    }

    @GetMapping("/roles/status/{status}")
    public ResponseEntity<ApiResponse<List<RbacRoleResponse>>> getByStatus(@PathVariable String status) {
        log.info("Fetching RBAC roles by status: status={}", status);
        List<RbacRoleResponse> response = roleService.getRolesByStatus(status);
        log.debug("Fetched {} RBAC roles for status={}", response.size(), status);
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLES_FETCHED, response));
    }

    @GetMapping("/roles/parent/{parentId}/children")
    public ResponseEntity<ApiResponse<List<RbacRoleResponse>>> getChildRoles(@PathVariable Long parentId) {
        log.info("Fetching child roles: parentRoleId={}", parentId);
        List<RbacRoleResponse> response = roleService.getChildRoles(parentId);
        log.debug("Fetched {} child roles for parentRoleId={}", response.size(), parentId);
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLES_FETCHED, response));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        log.info("Soft deleting role: roleId={}", id);
        roleService.deleteRole(id);
        log.info("Role soft-deleted successfully: roleId={}", id);
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLE_DELETED));
    }

    /**
     * Changes the status of an existing role (ACTIVE ↔ INACTIVE).
     *
     * @param id      the primary key of the role
     * @param request the status change request containing new status
     */
    @PatchMapping("/roles/{id}/status")
    public ResponseEntity<ApiResponse<RbacRoleResponse>> changeRoleStatus(
            @PathVariable Long id,
            @Valid @RequestBody RbacStatusChangeRequest request) {
        log.info("Change role status request received: roleId={}, status={}", id, request.getStatus());
        RbacRoleResponse response = roleService.changeRoleStatus(id, request.getStatus());
        log.info("Role status changed successfully: roleId={}, status={}", id, response.getStatus());
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLE_STATUS_CHANGED, response));
    }

    /**
     * Retrieves roles with optional pagination, sorting, global search, and dynamic filters.
     */
    @PostMapping("/role/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<RbacRoleResponse>>> listRoles(
            @Valid @RequestBody(required = false) RoleListRequestDto request) {
        RoleListRequestDto listRequest = request != null ? request : new RoleListRequestDto();
        log.info("Fetching RBAC roles with filters: page={}, size={}, search={}",
                listRequest.getPage(), listRequest.getSize(), listRequest.getSearch());
        PaginatedResponse<RbacRoleResponse> response = roleService.listRoles(request);
        return ResponseEntity.ok(ApiResponse.ok(ApiMessageConstants.DATA_FETCHED, response));
    }

}

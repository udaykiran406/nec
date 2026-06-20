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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rbac")
@AllArgsConstructor
public class RbacRoleController {

    private final RoleService roleService;

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<RbacRoleResponse>> createRole(
            @Valid @RequestBody RbacRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(RbacConstants.ROLE_CREATED, roleService.createRole(request)));
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<RbacRoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RbacRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLE_UPDATED, roleService.updateRole(id, request)));
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<RbacRoleResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLE_FETCHED, roleService.getRoleById(id)));
    }

    @GetMapping("/roles/all")
    public ResponseEntity<ApiResponse<List<RbacRoleResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLES_FETCHED, roleService.getAllRoles()));
    }

    @GetMapping("/roles/status/{status}")
    public ResponseEntity<ApiResponse<List<RbacRoleResponse>>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLES_FETCHED, roleService.getRolesByStatus(status)));
    }

    @GetMapping("/roles/parent/{parentId}/children")
    public ResponseEntity<ApiResponse<List<RbacRoleResponse>>> getChildRoles(@PathVariable Long parentId) {
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLES_FETCHED, roleService.getChildRoles(parentId)));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
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
        RbacRoleResponse response = roleService.changeRoleStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok(RbacConstants.ROLE_STATUS_CHANGED, response));
    }

    /**
     * Retrieves roles with optional pagination, sorting, global search, and dynamic filters.
     */
    @PostMapping("/role/list")
    public ResponseEntity<ApiResponse<PaginatedResponse<RbacRoleResponse>>> listRoles(
            @Valid @RequestBody(required = false) RoleListRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok(ApiMessageConstants.DATA_FETCHED, roleService.listRoles(request)));
    }

}

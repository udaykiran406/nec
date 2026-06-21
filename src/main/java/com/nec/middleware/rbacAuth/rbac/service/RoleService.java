package com.nec.middleware.rbacAuth.rbac.service;

import com.nec.middleware.rbacAuth.rbac.dto.request.RbacRoleRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.RoleListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRoleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;

import java.util.List;

/**
 * Service interface for Role-related operations.
 * Handles CRUD operations, status management, and hierarchical role queries.
 */
public interface RoleService {

    /**
     * Creates a new role record (INSERT).
     */
    RbacRoleResponse createRole(RbacRoleRequest request);

    /**
     * Updates an existing role record (UPDATE).
     *
     * @param id      the primary key of the role (path variable)
     * @param request the updated role data
     */
    RbacRoleResponse updateRole(Long id, RbacRoleRequest request);

    /**
     * Retrieves a single non-deleted role by its primary key.
     */
    RbacRoleResponse getRoleById(Long roleId);

    /**
     * Retrieves all non-deleted roles, ordered alphabetically by name.
     */
    List<RbacRoleResponse> getAllRoles();

    /**
     * Retrieves roles using optional pagination, sorting, search, and dynamic filters.
     *
     * @param request the role list request DTO
     * @return paginated or full role list response
     */
    PaginatedResponse<RbacRoleResponse> listRoles(RoleListRequestDto request);

    /**
     * Retrieves all non-deleted roles matching a specific status ('ACTIVE' or 'INACTIVE').
     */
    List<RbacRoleResponse> getRolesByStatus(String status);

    /**
     * Retrieves all child roles of a given parent role.
     */
    List<RbacRoleResponse> getChildRoles(Long parentRoleId);

    /**
     * Soft-deletes a role (isDeleted = true). Row is NOT physically removed.
     */
    void deleteRole(Long roleId);

    /**
     * Changes the status of an existing role (e.g. ACTIVE → INACTIVE or vice versa).
     *
     * @param roleId the primary key of the role
     * @param status the new status ('ACTIVE' or 'INACTIVE')
     * @return the updated role as a response DTO
     */
    RbacRoleResponse changeRoleStatus(Long roleId, String status);
}


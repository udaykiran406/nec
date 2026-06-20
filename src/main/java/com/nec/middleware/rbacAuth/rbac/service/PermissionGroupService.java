package com.nec.middleware.rbacAuth.rbac.service;

import com.nec.middleware.rbacAuth.rbac.dto.request.RbacModuleRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacPermissionGroupRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.ModuleListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.request.GroupListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacModuleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacGroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacPermissionGroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacPermissionGroupModuleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;

import java.util.List;

/**
 * Service interface for Permission Group, Module, and Permission operations.
 * Handles hierarchical permission group management and module/group/permission CRUD operations.
 */
public interface PermissionGroupService {

    // =========================================================================
    // PERMISSION GROUP OPERATIONS
    // =========================================================================

    /**
     * Creates a new permission group hierarchy (modules, groups, permissions).
     * All IDs in the request should be null (new records).
     */
    ApiResponse<RbacPermissionGroupResponse> createPermissionGroup(RbacPermissionGroupRequest request);

    /**
     * Updates an existing permission group hierarchy (modules, groups, permissions).
     * All IDs in the request must be present (existing records).
     */
    ApiResponse<RbacPermissionGroupResponse> updatePermissionGroup(RbacPermissionGroupRequest request);

    /**
     * Retrieves a specific module's permission group with its complete hierarchy (groups and permissions).
     * Only returns non-deleted modules, groups, and permissions.
     *
     * @param moduleId the primary key of the module
     * @return the module with its complete permission group hierarchy
     */
    RbacPermissionGroupResponse getPermissionGroupByModuleId(Long moduleId);

    /**
     * Retrieves all modules with their complete permission group hierarchies (groups and permissions).
     * Only returns non-deleted modules, groups, and permissions, ordered by module display order.
     *
     * @return list of all modules with their permission group hierarchies
     */
    RbacPermissionGroupResponse getAllPermissionGroups();

    /**
     * Retrieves permission group hierarchy with optional pagination, sorting, search, and module-level filters.
     *
     * @param request the module list request DTO (module-level filters)
     * @return paginated or full hierarchical module list response
     */
    PaginatedResponse<RbacPermissionGroupModuleResponse> listPermissionGroups(ModuleListRequestDto request);

    // =========================================================================
    // MODULE OPERATIONS
    // =========================================================================

    /**
     * Unified method to save (create or update) a module.
     * If {@code request.moduleId} is {@code null}, creates a new module (INSERT).
     * If {@code request.moduleId} is non-null, updates the existing module (UPDATE).
     *
     * @param request the module request DTO
     * @return the created/updated module as a response DTO
     */
    RbacModuleResponse saveModule(RbacModuleRequest request);

    /**
     * Creates a new module record (INSERT).
     */
    RbacModuleResponse createModule(RbacModuleRequest request);

    /**
     * Updates an existing module record (UPDATE).
     */
    RbacModuleResponse updateModule(RbacModuleRequest request);

    /**
     * Retrieves a single non-deleted module by its primary key.
     */
    RbacModuleResponse getModuleById(Long moduleId);

    /**
     * Retrieves all non-deleted modules, ordered by display order.
     */
    List<RbacModuleResponse> getAllModules();

    /**
     * Retrieves all non-deleted modules matching a specific status ('ACTIVE' or 'INACTIVE').
     */
    List<RbacModuleResponse> getModulesByStatus(String status);

    /**
     * Soft-deletes a module (isDeleted = true). Row is NOT physically removed.
     */
    void deleteModule(Long moduleId);

    /**
     * Retrieves modules using optional pagination, sorting, search, and dynamic filters.
     *
     * @param request the module list request DTO
     * @return paginated or full module list response
     */
    PaginatedResponse<RbacModuleResponse> listModules(ModuleListRequestDto request);

    // =========================================================================
    // PERMISSION GROUP (Groups) OPERATIONS
    // =========================================================================

    /**
     * Retrieves permission groups using optional pagination, sorting, search, and dynamic filters.
     *
     * @param request the group list request DTO
     * @return paginated or full permission group list response
     */
    PaginatedResponse<RbacGroupResponse> listGroups(GroupListRequestDto request);
}


package com.nec.middleware.rbacAuth.rbac.service;

import com.nec.middleware.rbacAuth.rbac.dto.request.RbacRolePermissionRequest;
import com.nec.middleware.rbacAuth.rbac.dto.request.RolePermissionListRequestDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRolePermissionResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.RbacRolePermissionResponseDto;
import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;
import java.util.List;
/**

 * Service interface for Role-Permission mapping operations.

 * Handles creation, updating, deletion, and retrieval of role-permission associations.

 */

public interface RolePermissionService {
    /**

     * Creates new role-permission mappings. Fails if any mapping already exists.

     *

     * @param request the role permission request DTO
     */

    ApiResponse<RbacRolePermissionResponse> createRolePermission(RbacRolePermissionRequest request);

    /**

     * Updates (activates or adds) existing role-permission mappings.

     * Activates INACTIVE mappings; creates new ones if absent.

     *

     * @param request the role permission request DTO
     */

    ApiResponse<RbacRolePermissionResponse> updateRolePermission(RbacRolePermissionRequest request);

    /**

     * Updates the status of an existing role-permission mapping.

     *

     * @param rolePermissionId the primary key of the mapping to update
     * @param status           the new status ('ACTIVE' or 'INACTIVE')
     * @return the updated role-permission mapping DTO
     */

    RbacRolePermissionResponseDto updateRolePermissionStatus(Long rolePermissionId, String status);

    /**

     * Soft-deletes a role-permission mapping by setting status to 'INACTIVE'.

     * Returns the remaining active permissions for the role in hierarchical format.

     *

     * @param rolePermissionId the primary key of the mapping to delete
     * @return hierarchical role-permission response with remaining active permissions
     */

    RbacRolePermissionResponse deleteRolePermission(Long rolePermissionId);

    /**

     * Retrieves all active role-permission mappings for a specific role in hierarchical format.

     * Data is grouped by Module -> Group -> Permissions.

     *

     * @param roleId the role ID
     * @return hierarchical role-permission response
     */

    RbacRolePermissionResponse getRolePermissionsByRoleId(Long roleId);

    /**

     * Retrieves a specific role-permission mapping and returns all permissions for that role

     * in hierarchical format.

     *

     * @param rolePermissionId the primary key of the mapping
     * @return hierarchical role-permission response
     */

    RbacRolePermissionResponse getRolePermissionById(Long rolePermissionId);

    /**

     * Retrieves all active role-permission mappings for a specific role (flat list).

     *

     * @param roleId the role ID
     * @return list of role-permission mappings
     */

    List<RbacRolePermissionResponseDto> getRolePermissionsByRoleIdFlat(Long roleId);

    /**

     * Retrieves all role-permission mappings for a specific role and module.

     *

     * @param roleId   the role ID
     * @param moduleId the module ID
     * @return list of role-permission mappings
     */

    List<RbacRolePermissionResponseDto> getRolePermissionsByRoleIdAndModuleId(Long roleId, Long moduleId);

    /**

     * Retrieves all active role-permission mappings in hierarchical format.

     * Data is grouped by Role -> Module -> Group -> Permissions.

     *

     * @return list of hierarchical role permission responses (one per role)
     */

    List<RbacRolePermissionResponse> getAllRolePermissions();

    /**

     * Retrieves role-permission mappings with optional pagination, sorting, and dynamic filters.

     *

     * @param request the role-permission list request DTO
     * @return paginated or full flat role-permission mapping list response
     */

    PaginatedResponse<RbacRolePermissionResponseDto> listRolePermissions(RolePermissionListRequestDto request);
}

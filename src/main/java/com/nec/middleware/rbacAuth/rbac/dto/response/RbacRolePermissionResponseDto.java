package com.nec.middleware.rbacAuth.rbac.dto.response;

import com.nec.middleware.rbacAuth.rbac.dto.response.nested.GroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.ModuleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.PermissionResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.RoleResponse;
import lombok.*;

/**
 * Flat response DTO for role-permission mappings with nested business identifiers.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacRolePermissionResponseDto {

    private RoleResponse role;
    private ModuleResponse module;
    private GroupResponse group;
    private PermissionResponse permission;
    private String status;
}

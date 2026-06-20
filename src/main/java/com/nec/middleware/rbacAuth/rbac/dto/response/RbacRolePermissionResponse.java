package com.nec.middleware.rbacAuth.rbac.dto.response;

import com.nec.middleware.rbacAuth.rbac.dto.response.nested.GroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.ModuleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.PermissionResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.RoleResponse;
import lombok.*;

import java.util.List;

/**
 * Response DTO for hierarchical role permission mappings.
 * Groups role permissions by Role -> Module -> Group -> Permission hierarchy.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacRolePermissionResponse {

    private RoleResponse role;
    private List<ModulePermissionResponse> modules;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModulePermissionResponse {

        private ModuleResponse module;
        private List<GroupPermissionResponse> groups;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupPermissionResponse {

        private GroupResponse group;
        private List<PermissionMappingResponse> permissions;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PermissionMappingResponse {

        private PermissionResponse permission;
    }
}

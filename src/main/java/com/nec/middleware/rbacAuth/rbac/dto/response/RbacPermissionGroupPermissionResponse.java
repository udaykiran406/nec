package com.nec.middleware.rbacAuth.rbac.dto.response;

import com.nec.middleware.rbacAuth.rbac.dto.response.nested.GroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.ModuleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacPermissionGroupPermissionResponse {

    private String permissionCode;
    private String permissionName;
    private String description;
    private Integer displayOrder;
    private String status;
    private Boolean isSideMenu;
    private GroupResponse group;
    private ModuleResponse module;
}

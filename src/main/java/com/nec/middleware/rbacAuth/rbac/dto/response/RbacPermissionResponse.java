package com.nec.middleware.rbacAuth.rbac.dto.response;

import com.nec.middleware.rbacAuth.rbac.dto.response.nested.GroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.ModuleResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacPermissionResponse {

    private ModuleResponse module;
    private GroupResponse group;
    private String permissionCode;
    private String permissionName;
    private String description;
    private Integer displayOrder;
    private String status;
    private Boolean isSideMenu;
}

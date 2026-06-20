package com.nec.middleware.rbacAuth.rbac.dto.response;

import com.nec.middleware.rbacAuth.rbac.dto.response.nested.ModuleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacPermissionGroupGroupResponse {

    private String groupCode;
    private String groupName;
    private String description;
    private Integer displayOrder;
    private String status;
    private ModuleResponse module;
    private List<RbacPermissionGroupPermissionResponse> permissions;
}

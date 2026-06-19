package com.nec.middleware.rbacAuth.rbac.dto.response;

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
public class RbacPermissionGroupModuleResponse {

    private String moduleCode;
    private String moduleName;
    private String description;
    private Integer displayOrder;
    private String status;
    private List<RbacPermissionGroupGroupResponse> groups;
}

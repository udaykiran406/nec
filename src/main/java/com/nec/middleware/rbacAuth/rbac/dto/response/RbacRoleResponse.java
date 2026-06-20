package com.nec.middleware.rbacAuth.rbac.dto.response;

import com.nec.middleware.rbacAuth.rbac.dto.response.nested.RoleResponse;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacRoleResponse {

    private String roleCode;
    private String roleName;
    private String description;
    private RoleResponse parentRole;
    private BigDecimal approvalLimit;
    private Boolean isParentRole;
    private String status;
}

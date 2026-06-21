package com.nec.middleware.rbacAuth.rbac.dto.response.nested;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {

    private Long roleId;
    private String roleCode;
    private String roleName;
}

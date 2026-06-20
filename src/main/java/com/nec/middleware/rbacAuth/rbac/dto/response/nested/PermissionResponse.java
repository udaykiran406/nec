package com.nec.middleware.rbacAuth.rbac.dto.response.nested;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {

    private String permissionCode;
    private String permissionName;
}

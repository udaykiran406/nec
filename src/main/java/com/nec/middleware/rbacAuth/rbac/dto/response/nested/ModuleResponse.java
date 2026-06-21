package com.nec.middleware.rbacAuth.rbac.dto.response.nested;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponse {

    private String moduleCode;
    private String moduleName;
}

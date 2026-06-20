package com.nec.middleware.rbacAuth.rbac.dto.response;

import lombok.*;

/**
 * Response DTO returned by all Module (nec_rbac_module) API endpoints.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacModuleResponse {

    private String moduleCode;
    private String moduleName;
    private Integer displayOrder;
    private String status;
}

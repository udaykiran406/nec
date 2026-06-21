package com.nec.middleware.rbacAuth.rbac.dto.response;

import com.nec.middleware.rbacAuth.rbac.dto.response.nested.ModuleResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacGroupResponse {

    private ModuleResponse module;
    private String groupCode;
    private String groupName;
    private String description;
    private Integer displayOrder;
    private String status;
}

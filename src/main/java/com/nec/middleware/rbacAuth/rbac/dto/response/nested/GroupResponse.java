package com.nec.middleware.rbacAuth.rbac.dto.response.nested;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponse {

    private String groupCode;
    private String groupName;
}

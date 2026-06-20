package com.nec.middleware.rbacAuth.rbac.dto.response.nested;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionResponse {

    private Long id;
    private String regionName;
}

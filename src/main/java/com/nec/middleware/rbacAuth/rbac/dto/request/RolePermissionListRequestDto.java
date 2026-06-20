package com.nec.middleware.rbacAuth.rbac.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;
import java.time.LocalDate;
/**
 * Request DTO for paginated and filterable role-permission mapping list API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionListRequestDto {
    @Min(value = 0, message = "Page must be zero or greater.")
    private Integer page;

    @Min(value = 1, message = "Size must be at least 1.")
    private Integer size;

    private String sortBy;

    private String sortDirection;

    private String search;

    private Long rolePermissionId;

    private Long roleId;

    private Long moduleId;

    private Long groupId;

    private Long permissionId;

    private String roleName;

    private String moduleName;

    private String groupName;

    private String permissionName;

    private String status;

    private LocalDate fromDate;

    private LocalDate toDate;
}

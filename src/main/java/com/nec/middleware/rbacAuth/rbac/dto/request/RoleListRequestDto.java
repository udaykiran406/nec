package com.nec.middleware.rbacAuth.rbac.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

/**
 * Request DTO for paginated, searchable, and filterable role list API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleListRequestDto {

    @Min(value = 0, message = "Page must be zero or greater.")
    private Integer page;

    @Min(value = 1, message = "Size must be at least 1.")
    private Integer size;

    private String sortBy;

    private String sortDirection;

    private String search;

    private Long roleId;

    private Long parentRoleId;

    private String parentRoleName;

    private String roleCode;

    private String roleName;

    private String status;

    private Boolean isParentRole;

    private LocalDate fromDate;

    private LocalDate toDate;
}

package com.nec.middleware.rbacAuth.rbac.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

/**
 * Request DTO for paginated, searchable, and filterable user list API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListRequestDto {

    @Min(value = 0, message = "Page must be zero or greater.")
    private Integer page;

    @Min(value = 1, message = "Size must be at least 1.")
    private Integer size;

    private String sortBy;

    private String sortDirection;

    private String search;

    private String userId;

    private Long roleId;

    private String roleCode;

    private String roleName;

    private Long genderId;

    private Long departmentId;

    private Long regionId;

    private Long districtId;

    private Long cityId;

    private String userName;

    private String email;

    private String mobileNumber;

    private Integer isActive;

    private LocalDate fromDate;

    private LocalDate toDate;
}

package com.nec.middleware.portal.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortalUserResponseDto {

    private Long id;
    private String userName;
    private String code;
    private Long genderId;
    private Long roleId;
    private String phone;
    private String email;
    private String photoPath;
    private String faculty;
    private Long departmentId;
    private Long regionId;
    private Long districtId;
    private Long cityId;
    private Long portalUserTypeId;
    private Long referenceId;

    // Audit fields
    private Boolean isActive;
    private Boolean isDeleted;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}

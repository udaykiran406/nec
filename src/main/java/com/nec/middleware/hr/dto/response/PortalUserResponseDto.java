package com.nec.middleware.hr.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortalUserResponseDto {

    // ------------------------------------------------------------------ Identity
    private Long id;
    private String portalUserId;
    private String userName;
    private String phone;
    private String email;
    private String photoPath;
    private String faculty;

    // ------------------------------------------------------------------ Lookup FKs (ID + resolved name)
    private Long genderId;
    private String genderName;

    private Long roleId;
    private String roleName;

    private Long portalUserTypeId;
    private String portalUserTypeName;

    // ------------------------------------------------------------------ Master Data FKs (ID + resolved name)
    private Long universityId;
    private String universityName;

    private Long regionId;
    private String regionName;

    private Long districtId;
    private String districtName;

    private Long cityId;
    private String cityName;

//    private Long referenceId;
//    private String referenceName;

    // ------------------------------------------------------------------ Audit
    private Boolean isActive;
    private Boolean isDeleted;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
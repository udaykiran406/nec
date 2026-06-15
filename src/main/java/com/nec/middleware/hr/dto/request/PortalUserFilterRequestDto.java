package com.nec.middleware.hr.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortalUserFilterRequestDto {

    private String portalUserId;
    private String userName;
    private Long roleId;
    private Long genderId;
    private Long regionId;
    private Long districtId;
    private Long cityId;
    private Long portalUserTypeId;
    private Boolean isActive;

    // Pagination
//    @Min(value = 0, message = "Page index must be 0 or greater")
//    private int page = 0;
//
//    @Min(value = 1, message = "Page size must be at least 1")
//    @Max(value = 100, message = "Page size must not exceed 100")
//    private int size = 20;
}

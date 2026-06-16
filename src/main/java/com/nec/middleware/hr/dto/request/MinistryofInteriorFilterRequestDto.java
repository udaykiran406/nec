package com.nec.middleware.hr.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinistryofInteriorFilterRequestDto {

    private String ministryofInteriorId;
    private Long moiTitleId;
    private Long regionId;
    private Long districtId;
    private Long cityId;
    private Long vrcId;
    private Long statusId;
    private Boolean isActive;
    private String Name;
    private Long genderId;
    private String phone;
    private String email;
}
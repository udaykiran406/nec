package com.nec.middleware.hr.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AaqilFilterRequestDto {

    private String aaqilId;
    private Long aaqilTypeId;
    private Long regionId;
    private Long districtId;
    private Long cityId;
    private Long statusId;
    private Boolean isActive;
    private String fullName;
    private Long genderId;
    private String phone;
    private String email;
}
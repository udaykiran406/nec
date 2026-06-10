package com.nec.middleware.hr.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AaqilResponseDto {

    private Long          id;
    private String        code;
    private Long          aaqilTypeId;
    private String        fullName;
    private Long          genderId;
    private Short         age;
    private String        phone;
    private String        email;
    private Long          regionId;
    private Long          districtId;
    private Long          cityId;
    private Long          statusId;
    private Boolean       isActive;
    private Boolean       isDeleted;
    private Long          createdBy;
    private LocalDateTime createdAt;
    private Long          updatedBy;
    private LocalDateTime updatedAt;
}

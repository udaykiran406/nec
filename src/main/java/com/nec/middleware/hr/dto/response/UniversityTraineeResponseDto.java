package com.nec.middleware.hr.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityTraineeResponseDto {

    private Long          id;
    private String        code;
    private String        fullName;
    private Long          genderId;
    private Short         age;
    private String        phone;
    private String        email;
    private Long          paymentMethodId;
    private Long          universityId;
    private String        semester;
    private String        faculty;
    private Long          regionId;
    private Long          districtId;
    private Long          cityId;
    private String        photoUrl;
    private Long          statusId;
    private Boolean       isActive;
    private Boolean       isDeleted;
    private String          createdBy;
    private LocalDateTime createdAt;
    private String          updatedBy;
    private LocalDateTime updatedAt;
}

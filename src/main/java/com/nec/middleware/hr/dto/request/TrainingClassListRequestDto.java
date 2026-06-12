package com.nec.middleware.hr.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingClassListRequestDto {

    private String classCode;
    private String className;
    private String location;
    private Long trainingTypeId;
    private Long statusId;
    private Long regionId;
    private Long districtId;
    private Long cityId;
    private Long universityId;
    private Long trainerTotId;
}

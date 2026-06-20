package com.nec.middleware.hr.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingTraineeAllocationFilterRequestDto {

    private String allocationCode;

    private Long traineeId;

    private Long regionId;

    private Long districtId;

    private Long cityId;

    private Long universityId;

    private Long trainingClassId;

    private Long trainingTypeId;

    private Long statusId;

    private Boolean isActive;
}
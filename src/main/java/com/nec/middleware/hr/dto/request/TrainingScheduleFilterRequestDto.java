package com.nec.middleware.hr.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingScheduleFilterRequestDto {

    private String scheduleCode;

    private Long trainingClassId;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String venue;

    private String location;

    private Boolean isActive;
}
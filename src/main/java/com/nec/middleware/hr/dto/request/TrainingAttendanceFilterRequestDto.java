package com.nec.middleware.hr.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingAttendanceFilterRequestDto {

    private Long trainingClassId;

    private Long traineeId;

    private LocalDate fromDate;

    private LocalDate toDate;

    private Boolean isPresent;

    private Boolean isActive;
}
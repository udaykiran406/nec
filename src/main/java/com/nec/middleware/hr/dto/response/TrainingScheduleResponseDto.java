package com.nec.middleware.hr.dto.response;

import com.nec.middleware.dto.IdValueDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingScheduleResponseDto {

    private Long id;

    private String scheduleCode;

    private IdValueDto trainingClass;

    private LocalDate fromDate;

    private LocalDate toDate;

    private Integer duration;

    private String timeSlot;

    private String venue;

    private String location;

    private Boolean isActive;

    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}
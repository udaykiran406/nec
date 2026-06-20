package com.nec.middleware.hr.dto.response;

import com.nec.middleware.dto.IdValueDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingTraineeAllocationResponseDto {

    private Long id;
    private String allocationCode;

    private IdValueDto trainee;
    private IdValueDto university;

    private IdValueDto region;
    private IdValueDto district;
    private IdValueDto city;

    private String faculty;

    private IdValueDto trainingClass;
    private IdValueDto trainingType;

    private IdValueDto status;

    private LocalDate allocationDate;
    private String notes;

    private Boolean isActive;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
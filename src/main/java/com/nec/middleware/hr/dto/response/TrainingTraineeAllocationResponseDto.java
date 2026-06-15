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
public class TrainingTraineeAllocationResponseDto {

    private Long id;

    private String allocationCode;

    private LocalDate allocationDate;

    private String notes;

    // Trainee
    private IdValueDto trainee;

    // Derived from trainee
    private IdValueDto university;
    private IdValueDto region;
    private String faculty;

    // Training class
    private IdValueDto trainingClass;

    // Derived from training class
    private IdValueDto trainingType;

    // Status
    private IdValueDto status;

    // Audit
    private Boolean isActive;

    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;
}
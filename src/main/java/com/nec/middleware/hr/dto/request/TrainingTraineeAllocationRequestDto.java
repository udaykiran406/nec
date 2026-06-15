package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingTraineeAllocationRequestDto {

    private String allocationCode;

    @NotNull(message = "Trainee is required")
    private Long traineeId;

    @NotNull(message = "Training class is required")
    private Long trainingClassId;

    @NotNull(message = "Allocation date is required")
    private LocalDate allocationDate;

    @NotNull(message = "Status is required")
    private Long statusId;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    private String createdBy;
    private String updatedBy;
}
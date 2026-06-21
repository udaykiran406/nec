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

    @NotNull(message = "Region is required")
    private Long regionId;

    @NotNull(message = "District is required")
    private Long districtId;

    @NotNull(message = "City is required")
    private Long cityId;

    @NotNull(message = "University is required")
    private Long universityId;

    @NotNull(message = "Trainee is required")
    private Long traineeId;

    @Size(max = 150, message = "Faculty cannot exceed 150 characters")
    private String faculty;

    @NotNull(message = "Training class is required")
    private Long trainingClassId;

    @NotNull(message = "Training type is required")
    private Long trainingTypeId;

    @NotNull(message = "Status is required")
    private Long statusId;

    @NotNull(message = "Allocation date is required")
    private LocalDate allocationDate;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
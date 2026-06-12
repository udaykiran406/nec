package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingClassRequest {

    private Long id; // null = create, present = update

    @NotBlank(message = "Class name is required")
    private String className;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than zero")
    private Integer capacity;

    @NotNull(message = "Training type is required")
    private Long trainingTypeId;

    @NotNull(message = "Region is required")
    private Long regionId;

    @NotNull(message = "District is required")
    private Long districtId;

    @NotNull(message = "City is required")
    private Long cityId;

    @NotNull(message = "University is required")
    private Long universityId;

    @NotNull(message = "Trainer TOT is required")
    private Long trainerTotId;

    @NotNull(message = "Status is required")
    private Long statusId;

    private String description;

    private String preRequests;
}
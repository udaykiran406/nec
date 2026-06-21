package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingScheduleRequestDto {

    @NotNull(message = "Training class is required")
    private Long trainingClassId;

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;

    @NotBlank(message = "Time slot is required")
    @Size(max = 100, message = "Time slot must not exceed 100 characters")
    private String timeSlot;

    @NotBlank(message = "Venue is required")
    @Size(max = 200, message = "Venue must not exceed 200 characters")
    private String venue;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;
}
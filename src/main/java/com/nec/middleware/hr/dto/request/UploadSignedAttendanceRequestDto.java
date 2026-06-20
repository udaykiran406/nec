package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadSignedAttendanceRequestDto {

    @NotNull(message = "Training class is required")
    private Long trainingClassId;

    @NotNull(message = "University is required")
    private Long universityId;

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;

    private String uploadedBy;
}
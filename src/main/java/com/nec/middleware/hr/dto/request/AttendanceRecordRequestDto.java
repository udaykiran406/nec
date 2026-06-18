package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecordRequestDto {

    @NotNull(message = "University trainee is required")
    private Long universityTraineeId;

    @NotNull(message = "Attendance status is required")
    private Boolean isPresent;
}
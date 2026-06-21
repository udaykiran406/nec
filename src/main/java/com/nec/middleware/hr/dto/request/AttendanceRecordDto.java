package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecordDto {

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotNull(message = "Attendance status is required")
    private String status;
}
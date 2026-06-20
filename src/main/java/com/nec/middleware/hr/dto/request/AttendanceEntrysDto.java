package com.nec.middleware.hr.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceEntrysDto {

    @NotNull(message = "Trainee is required")
    private Long traineeId;

    @Valid
    @NotEmpty(message = "Attendance records are required")
    private List<AttendanceRecordDto> attendanceRecords;
}
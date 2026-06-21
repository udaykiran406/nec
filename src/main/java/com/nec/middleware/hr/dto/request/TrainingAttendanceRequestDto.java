package com.nec.middleware.hr.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingAttendanceRequestDto {

    @NotNull(message = "Training class is required")
    private Long trainingClassId;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    private String signedSheetUrl;

    @Valid
    @NotEmpty(message = "Attendance records are required")
    private List<AttendanceRecordRequestDto> attendanceRecords;
}
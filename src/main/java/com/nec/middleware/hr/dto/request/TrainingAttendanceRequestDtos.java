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
public class TrainingAttendanceRequestDtos {

    @NotNull(message = "Training class is required")
    private Long classId;

    @NotNull(message = "University is required")
    private Long universityId;

    private String markedBy;

    @Valid
    @NotEmpty(message = "Attendance entries are required")
    private List<AttendanceEntrysDto> attendanceEntries;
}
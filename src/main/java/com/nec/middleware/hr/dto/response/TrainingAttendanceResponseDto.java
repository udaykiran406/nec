package com.nec.middleware.hr.dto.response;

import com.nec.middleware.dto.IdValueDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingAttendanceResponseDto {

    private IdValueDto trainingClass;

    private LocalDate attendanceDate;

    private String signedSheetUrl;

    private List<AttendanceRecordResponseDto> attendanceRecords;
}
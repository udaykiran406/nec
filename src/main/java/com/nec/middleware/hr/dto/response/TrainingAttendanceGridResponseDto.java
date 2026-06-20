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
public class TrainingAttendanceGridResponseDto {

    private Long classId;

    private String className;

    private IdValueDto universityId;

    private List<LocalDate> scheduleDates;

    private List<TraineeAttendanceGridDto> trainees;
}
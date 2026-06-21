package com.nec.middleware.hr.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveTrainingAttendanceResponseDto {

    private Boolean success;

    private String message;

    private AttendanceSaveSummaryDto summary;
}
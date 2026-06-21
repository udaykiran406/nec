package com.nec.middleware.hr.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSaveSummaryDto {

    private Integer totalTrainees;

    private Integer totalAttendanceRecords;

    private Integer savedRecords;
}
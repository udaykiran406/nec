package com.nec.middleware.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeAttendanceGridDto {

    private Long traineeId;

    private String traineeCode;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Map<String, String> attendance;

    private Integer daysAttended;

    private Integer totalScheduledDays;
}
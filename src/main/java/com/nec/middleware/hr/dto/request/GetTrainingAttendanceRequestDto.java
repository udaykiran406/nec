package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetTrainingAttendanceRequestDto {

    @NotNull(message = "University is required")
    private Long universityId;

    @NotNull(message = "Training class is required")
    private Long classId;
}
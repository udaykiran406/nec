package com.nec.middleware.hr.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignedAttendanceResponseDto {

    private Long id;

    private Long trainingClassId;

    private Long universityId;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String filePath;

    private String uploadedBy;
}
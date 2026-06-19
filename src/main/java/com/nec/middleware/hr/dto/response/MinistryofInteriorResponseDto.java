package com.nec.middleware.hr.dto.response;

import com.nec.middleware.dto.IdValueDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinistryofInteriorResponseDto {

    private String ministryofInteriorId;

    private String Name;
    private Short age;
    private String phone;
    private String email;
    private String photoPath;
    // ------------------------------------------------------------------ Lookups → IdValueDto
    private IdValueDto moiTitle;;
    private IdValueDto gender;
    private IdValueDto status;

    // ------------------------------------------------------------------ Master Data → IdValueDto
    private IdValueDto region;
    private IdValueDto district;
    private IdValueDto city;
    private IdValueDto vrc;
    // ------------------------------------------------------------------ Audit
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
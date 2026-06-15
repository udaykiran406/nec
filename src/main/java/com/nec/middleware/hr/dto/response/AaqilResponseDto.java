package com.nec.middleware.hr.dto.response;

import com.nec.middleware.dto.IdValueDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AaqilResponseDto {

    private String aaqilId;

    private String fullName;
    private Short age;
    private String phone;
    private String email;

    // ------------------------------------------------------------------ Lookups → IdValueDto
    private IdValueDto aaqilType;
    private IdValueDto gender;
    private IdValueDto status;

    // ------------------------------------------------------------------ Master Data → IdValueDto
    private IdValueDto region;
    private IdValueDto district;
    private IdValueDto city;

    // ------------------------------------------------------------------ Audit
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
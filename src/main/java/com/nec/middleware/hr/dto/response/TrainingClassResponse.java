package com.nec.middleware.hr.dto.response;

import com.nec.middleware.dto.IdValueDto;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingClassResponse {

    private Long id;

    private String classCode;

    private String className;
    private Integer capacity;
    private String location;

    private String description;
    private String preRequests;

    // Training Type
    private IdValueDto trainingType;

    // Region
    private IdValueDto region;

    // District
    private IdValueDto district;

    // City
    private IdValueDto city;

    // University
    private IdValueDto university;

    // Trainer TOT
    private IdValueDto trainerTot;

    // Status
    private IdValueDto status;

    // Audit
    private Boolean isActive;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
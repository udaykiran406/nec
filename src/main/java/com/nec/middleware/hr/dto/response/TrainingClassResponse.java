package com.nec.middleware.hr.dto.response;

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
    private Long trainingTypeId;
    private String trainingTypeName;

    // Region
    private Long regionId;
    private String regionName;

    // District
    private Long districtId;
    private String districtName;



    // City
    private Long cityId;
    private String cityName;

    // University
    private Long universityId;
    private String universityName;

    // Trainer TOT
    private Long trainerTotId;
    private String trainerTotName;

    // Status
    private Long statusId;
    private String statusName;

    // Audit
    private Boolean isActive;

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
package com.nec.middleware.masterdata.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AaqilTypeResponse {

    private Long id;

    private String code;

    private String value;

    private String description;

    private Boolean isActive;

    private Boolean isDeleted;

    private String createdBy;

    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
package com.nec.middleware.masterdata.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeploymentRoleResponse {

    private Long id;

    private String roleName;

    private String status;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Short isDeleted;
}
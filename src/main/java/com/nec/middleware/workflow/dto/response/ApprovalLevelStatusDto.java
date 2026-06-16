package com.nec.middleware.workflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalLevelStatusDto {

    private Integer approvalLevel;

    private String approvalRole;

    private String status;

    private String actionBy;

    private LocalDateTime actionDate;

    private String remarks;
}

package com.nec.middleware.workflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowInboxDto {

    private LocalDateTime createdDateTime;
    private String taskId;
    private String entityId;

    private String moduleName;

    private String requestedBy;

    private String processInstanceId;

    private String status;

    private String currentApprovalRole;

}

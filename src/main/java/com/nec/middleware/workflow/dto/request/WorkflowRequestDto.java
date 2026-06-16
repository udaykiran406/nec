package com.nec.middleware.workflow.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowRequestDto {
    private String taskId;
    private Boolean approved;

    private String remarks;
}

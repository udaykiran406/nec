package com.nec.middleware.workflow.dto.request;

import com.nec.middleware.workflow.Enum.WorkflowAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowActionRequestDto {

    @NotBlank
    private String taskId;

    @NotNull
    private WorkflowAction action;

    private String remarks;
}

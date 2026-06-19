package com.nec.middleware.workflow.mapper;

import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.workflow.dto.response.WorkflowInboxDto;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

@Component
public class WorkflowInboxMapper {


    public WorkflowInboxDto workflowSubmissionResponseDto(
            String referenceId,String moduleName, String currentApproval, String processInstanceId, String status) {

        return WorkflowInboxDto.builder()
                .entityId(referenceId)
                .moduleName(moduleName)
                .currentApprovalRole(currentApproval)
                .processInstanceId(processInstanceId)
                .status(status)
                .build();
    }

}

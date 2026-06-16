package com.nec.middleware.workflow.mapper;

import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.hr.entity.TemporaryContract;
import com.nec.middleware.workflow.dto.response.WorkflowSubmissionResponseDto;
import org.springframework.stereotype.Component;

@Component
public class WorkflowSubmissionResponseDtoMapper {


    public WorkflowSubmissionResponseDto workflowSubmissionResponseDto(
            TemporaryContract contract,String moduleName) {

        return WorkflowSubmissionResponseDto.builder()
                .referenceId(contract.getContractId())
                .moduleName(moduleName)
                .currentApproval(contract.getCurrentApproval())
                .processInstanceId(contract.getProcessInstanceId())
                .status(contract.getStatus() != null
                        ? IdValueDto.builder()
                        .id(contract.getStatus().getId())
                        .value(contract.getStatus().getValue())
                        .build()
                        : null)
                .build();
    }

}

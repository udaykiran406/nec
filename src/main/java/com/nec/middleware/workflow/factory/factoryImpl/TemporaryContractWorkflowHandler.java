package com.nec.middleware.workflow.factory.factoryImpl;

import com.nec.middleware.constants.Constants;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.dto.response.TemporaryContractResponseDto;
import com.nec.middleware.hr.entity.TemporaryContract;
import com.nec.middleware.hr.mapper.TemporaryContractMapper;
import com.nec.middleware.hr.repository.TemporaryContractRepository;
import com.nec.middleware.workflow.factory.WorkflowModuleHandler;
import com.nec.middleware.workflow.service.WorkflowAuditService;
import com.nec.middleware.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemporaryContractWorkflowHandler implements WorkflowModuleHandler {

    private final TemporaryContractRepository temporaryContractRepository;

    private final TemporaryContractMapper temporaryContractMapper;

    private final WorkflowAuditService workflowAuditService;

    @Override
    public String getModuleName() {
        return  Constants.TEMPORARY_CONTRACT_WORKFLOW_MODULE_NAME;
    }

    @Override
    public Object getDetails(String entityId) {

        TemporaryContract contract =
                temporaryContractRepository
                        .findByContractId(entityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contract not found while fetching with entityID "));

        TemporaryContractResponseDto temporaryContractResponseDto =
                temporaryContractMapper
                        .temporaryContractResponseDto(contract);

        temporaryContractResponseDto.setApprovalHistory(
                workflowAuditService.buildApprovalHistory(
                        Constants.TEMPORARY_CONTRACT_WORKFLOW_MODULE_NAME,
                        entityId));

        return temporaryContractResponseDto;
    }
}

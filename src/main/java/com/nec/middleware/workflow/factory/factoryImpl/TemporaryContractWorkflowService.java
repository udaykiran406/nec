package com.nec.middleware.workflow.factory.factoryImpl;

import com.nec.middleware.Lookups.repository.TemporaryContractStatusRepository;
import com.nec.middleware.constants.Constants;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.entity.TemporaryContract;
import com.nec.middleware.hr.repository.TemporaryContractRepository;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import com.nec.middleware.workflow.factory.WorkflowEntityService;
import com.nec.middleware.workflow.repository.WorkflowAuditRepository;
import com.nec.middleware.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("TEMPORARY_CONTRACT")
@RequiredArgsConstructor
public class TemporaryContractWorkflowService implements WorkflowEntityService {

    private final TemporaryContractRepository temporaryContractRepository;
    private final TemporaryContractStatusRepository temporaryContractStatusRepository;
    private final WorkflowAuditRepository workflowAuditRepository;

    private final WorkflowService workflowService;
    @Override
    public void moveToNextLevel(String moduleName,String entityId,String nextApprovalRole) {
        TemporaryContract contract =
                temporaryContractRepository.findByContractId(entityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contract not found"));

        contract.setCurrentApproval(nextApprovalRole);
        temporaryContractRepository.save(contract);
    }

    @Override
    public void markApproved( String entityId) {

        TemporaryContract contract =
                temporaryContractRepository.findByContractId(entityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contract not found"));
        contract.setCurrentApproval(null);
        contract.setStatus(Constants.WORKFLOW_APPROVED_STATUS);
        temporaryContractRepository.save(contract);
    }

}
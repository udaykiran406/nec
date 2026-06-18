package com.nec.middleware.workflow.factory.factoryImpl;

import com.nec.middleware.Lookups.repository.TemporaryContractStatusRepository;
import com.nec.middleware.constants.Constants;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.entity.TemporaryContract;
import com.nec.middleware.hr.repository.TemporaryContractRepository;
import com.nec.middleware.workflow.factory.WorkflowEntityService;
import com.nec.middleware.workflow.repository.WorkflowAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("TEMPORARY_CONTRACT")
@RequiredArgsConstructor
public class TemporaryContractWorkflowService implements WorkflowEntityService {
    private final TemporaryContractRepository temporaryContractRepository;
    private final TemporaryContractStatusRepository temporaryContractStatusRepository;
    private final WorkflowAuditRepository workflowAuditRepository;


    @Override
    public void moveToNextLevel(String entityId, String nextApprovalRole) {
        TemporaryContract contract =
                temporaryContractRepository.findByContractId(entityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contract not found for updating status for ID -->" + entityId));

        contract.setCurrentApproval(nextApprovalRole);
        temporaryContractRepository.save(contract);
    }

    @Override
    public void markApproved(String entityId) {

        TemporaryContract contract =
                temporaryContractRepository.findByContractId(entityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contract not found for updating status to approved " + entityId));
        contract.setCurrentApproval(null);
        contract.setStatus(Constants.WORKFLOW_APPROVED_STATUS);
        temporaryContractRepository.save(contract);
    }

    @Override
    public void markRejected(String entityId, String remarks) {

        TemporaryContract contract =
                temporaryContractRepository.findByContractId(entityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contract not found while fetching to update reject status " + entityId));
        contract.setStatus(Constants.WORKFLOW_REJECTED_STATUS);
        contract.setRejectedRemarks(remarks);
        contract.setCurrentApproval("No More Approve");
        temporaryContractRepository.save(contract);
    }

    @Override
    public void resubmit(String entityId, String firstApprovalRole, Integer revisionNo) {

        TemporaryContract contract =
                temporaryContractRepository.findByContractId(entityId).orElseThrow();

        contract.setRevisionNo(revisionNo);
        contract.setStatus(Constants.WORKFLOW_PENDING_STATUS);
        contract.setRejectedRemarks(null);
        contract.setCurrentApproval(firstApprovalRole);
        temporaryContractRepository.save(contract);
    }
}
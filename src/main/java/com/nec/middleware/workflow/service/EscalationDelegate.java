package com.nec.middleware.workflow.service;

import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import com.nec.middleware.workflow.entity.WorkflowEscalationMatrix;
import com.nec.middleware.workflow.repository.WorkflowAuditRepository;
import com.nec.middleware.workflow.repository.WorkflowEscalationMatrixRepository;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("escalationDelegate")
@RequiredArgsConstructor
public class EscalationDelegate implements JavaDelegate {
    private final WorkflowEscalationMatrixRepository escalationRepository;

    private final WorkflowAuditRepository auditRepository;

    @Override
    public void execute(DelegateExecution execution) {

        String moduleName =
                (String) execution.getVariable(
                        "moduleName");

        String currentRole =
                (String) execution.getVariable(
                        "approvalRole");

        String entityId =
                (String) execution.getVariable(
                        "entityId");

        WorkflowEscalationMatrix escalationMatrix =
                escalationRepository
                        .findByModuleNameAndApprovalRole(moduleName, currentRole)
                        .orElseThrow();

        execution.setVariable(
                "approvalRole",
                escalationMatrix.getEscalationRole());

        execution.setVariable(
                "currentApproval",
                escalationMatrix.getEscalationRole());

        WorkflowAudit audit =
                auditRepository
                        .findCurrentPendingAudit(moduleName,
                                entityId,
                                currentRole).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Module not found in workflow Audit table with pending status for entityId -->" + entityId + " and module --> " + moduleName));

        audit.setEscalated(true);
        audit.setEscalatedTo(escalationMatrix.getEscalationRole());
        audit.setEscalatedDate(LocalDateTime.now());
        auditRepository.save(audit);
    }
}
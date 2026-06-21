package com.nec.middleware.workflow.service;

import com.nec.middleware.constants.Constants;
import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import com.nec.middleware.masterdata.repository.ApprovalWorkflowLevelRepository;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import com.nec.middleware.workflow.factory.WorkflowEntityFactory;
import com.nec.middleware.workflow.factory.WorkflowEntityService;
import com.nec.middleware.workflow.repository.WorkflowAuditRepository;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("resubmissionDelegate")
@RequiredArgsConstructor
public class ResubmissionDelegate implements JavaDelegate {

    private final ApprovalWorkflowLevelRepository levelRepository;

    private final WorkflowAuditRepository auditRepository;

    private final WorkflowEntityFactory workflowEntityFactory;

    @Override
    public void execute(DelegateExecution execution) {

        String moduleName =
                (String) execution.getVariable("moduleName");

        String entityId =
                (String) execution.getVariable("entityId");

        String resubmittedBy =
                (String) execution.getVariable("actionBy");

        ApprovalWorkflowLevel firstLevel =
                levelRepository
                        .findByModuleNameAndLevelOrder(
                                moduleName,
                                1)
                        .orElseThrow();

        execution.setVariable(
                "currentApprovalLevel",
                1);

        execution.setVariable(
                "approvalRole",
                firstLevel.getApprovalRole());

        execution.setVariable(
                "currentApproval",
                firstLevel.getApprovalRole());

        WorkflowEntityService entityService =
                workflowEntityFactory.getService(moduleName);

        entityService.moveToNextLevel(
                entityId,
                firstLevel.getApprovalRole());

        Integer nextRevision = updateAudit(moduleName, entityId, resubmittedBy);
        entityService.resubmit(entityId, firstLevel.getApprovalRole(), nextRevision);

    }

    private Integer updateAudit(String moduleName, String entityId, String resubmittedBy) {

        Integer maxRevisionNo = auditRepository.findMaxRevisionNo(moduleName, entityId);

        Integer nextRevision =
                maxRevisionNo + 1;
        List<WorkflowAudit> latestAudits =
                auditRepository
                        .findByModuleNameAndEntityIdAndRevisionNoOrderByApprovalLevel(
                                moduleName, entityId, maxRevisionNo);

        for (WorkflowAudit audit : latestAudits) {
            WorkflowAudit copy = new WorkflowAudit();
            BeanUtils.copyProperties(
                    audit,
                    copy,
                    "id");
            copy.setRevisionNo(nextRevision);
            copy.setRemarks(null);
            copy.setEscalated(null);
            copy.setEscalatedTo(null);
            copy.setEscalatedDate(null);
            copy.setLastReminderDate(null);
            copy.setReminderCount(null);
            copy.setActionDate(null);
            copy.setAction(
                    copy.getApprovalLevel() == 1
                            ? Constants.WORKFLOW_PENDING_STATUS
                            : Constants.WORKFLOW_NOT_STARTED_STATUS);
            auditRepository.save(copy);
        }
        return nextRevision;
    }
}
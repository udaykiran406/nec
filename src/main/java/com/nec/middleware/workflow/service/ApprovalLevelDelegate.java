package com.nec.middleware.workflow.service;

import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import com.nec.middleware.masterdata.repository.ApprovalWorkflowLevelRepository;
import com.nec.middleware.workflow.factory.WorkflowEntityFactory;
import com.nec.middleware.workflow.factory.WorkflowEntityService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("approvalLevelDelegate")
@RequiredArgsConstructor
public class ApprovalLevelDelegate
        implements JavaDelegate {

    private final ApprovalWorkflowLevelRepository approvalWorkflowLevelRepository;

    private final WorkflowEntityFactory workflowEntityFactory;

    @Override
    public void execute(DelegateExecution execution) {

        String entityId =
                (String) execution.getVariable(
                        "entityId");

        Integer currentLevel =
                (Integer) execution.getVariable(
                        "currentApprovalLevel");

        String moduleName =
                (String) execution.getVariable(
                        "moduleName");

        Integer nextLevel =
                currentLevel + 1;

        Optional<ApprovalWorkflowLevel> level =
                approvalWorkflowLevelRepository
                        .findByModuleNameAndLevelOrder(moduleName,nextLevel);

        WorkflowEntityService workflowService =
                workflowEntityFactory.getService(moduleName);

        if (level.isPresent()) {

            execution.setVariable(
                    "currentApprovalLevel",
                    level.get().getLevelOrder());
            execution.setVariable(
                    "approvalRole",
                    level.get().getApprovalRole());
            execution.setVariable(
                    "currentApproval",
                    level.get().getApprovalRole());
            execution.setVariable(
                    "hasNextLevel",
                    true);

            workflowService.moveToNextLevel(moduleName,entityId,level.get().getApprovalRole());

        } else {

            execution.setVariable(
                    "hasNextLevel",
                    false);
            workflowService.markApproved(
                    entityId);
        }
    }
}
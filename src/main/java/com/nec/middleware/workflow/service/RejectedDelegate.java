package com.nec.middleware.workflow.service;

import com.nec.middleware.workflow.factory.WorkflowEntityFactory;
import com.nec.middleware.workflow.factory.WorkflowEntityService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("rejectedDelegate")
@RequiredArgsConstructor
public class RejectedDelegate implements JavaDelegate {

    private final WorkflowEntityFactory workflowEntityFactory;

    @Override
    public void execute(DelegateExecution execution) {

        String moduleName =
                (String) execution.getVariable("moduleName");

        String entityId =
                (String) execution.getVariable("entityId");

        String remarks =
                (String) execution.getVariable("remarks");

        String requesterRole =
                (String) execution.getVariable("requesterRole");

        WorkflowEntityService service =
                workflowEntityFactory.getService(moduleName);

        service.markRejected(entityId, remarks);

        execution.setVariable(
                "approvalRole",
                requesterRole);

        execution.setVariable(
                "currentApproval",
                requesterRole);
    }
}

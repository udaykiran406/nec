package com.nec.middleware.workflow.specification;

import com.nec.middleware.constants.Constants;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import org.springframework.data.jpa.domain.Specification;

public class WorkflowInboxSpecification {

    private WorkflowInboxSpecification() {
    }

    public static Specification<WorkflowAudit> buildSpecification(String role) {

        return (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("approvalRole"),role),
                        cb.equal(root.get("action"),
                                Constants.WORKFLOW_PENDING_STATUS));
    }
}

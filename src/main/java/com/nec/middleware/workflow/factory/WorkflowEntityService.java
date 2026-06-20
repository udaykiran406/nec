package com.nec.middleware.workflow.factory;

public interface WorkflowEntityService {

    void moveToNextLevel(String entityId, String nextApprovalRole);

    void markApproved(String entityId);

    void markRejected(String entityId, String remarks);

    void resubmit(String entityId, String firstApprovalRole, Integer revisionNo);
}

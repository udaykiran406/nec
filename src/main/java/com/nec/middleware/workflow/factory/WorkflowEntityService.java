package com.nec.middleware.workflow.factory;

public interface WorkflowEntityService {

    void moveToNextLevel(String entityId,String nextApprovalRole);

    void markApproved(String entityId);
}

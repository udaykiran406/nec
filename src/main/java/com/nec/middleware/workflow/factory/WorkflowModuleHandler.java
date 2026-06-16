package com.nec.middleware.workflow.factory;

public interface WorkflowModuleHandler {

    String getModuleName();

    Object getDetails(String entityId);
}

package com.nec.middleware.workflow.factory;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkflowEntityFactory {

    private final Map<String, WorkflowEntityService> services;

    public WorkflowEntityFactory(
            Map<String, WorkflowEntityService> services) {

        this.services = services;
    }

    public WorkflowEntityService getService(String moduleName) {

        WorkflowEntityService service = services.get(moduleName);

        if (service == null) {
            throw new IllegalArgumentException(
                    "No workflow service found for "
                            + moduleName);
        }

        return service;
    }
}

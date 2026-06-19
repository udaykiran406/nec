package com.nec.middleware.workflow.factory;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WorkflowModuleFactory {

    private final Map<String, WorkflowModuleHandler> handlers;

    public WorkflowModuleFactory(
            List<WorkflowModuleHandler> handlers) {

        this.handlers =
                handlers.stream()
                        .collect(Collectors.toMap(
                                WorkflowModuleHandler::getModuleName,
                                Function.identity()));
    }

    public WorkflowModuleHandler getHandler(String moduleName) {

        return handlers.get(moduleName);
    }
}
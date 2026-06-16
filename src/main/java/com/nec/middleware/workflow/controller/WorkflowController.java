package com.nec.middleware.workflow.controller;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.TaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workflow")
public class WorkflowController {

    private final TaskService taskService;

    @GetMapping("/activeTasks")
    public List<Map<String, Object>> getTasks() {

        return taskService.createTaskQuery()
                .active()
                .list()
                .stream()
                .map(task -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("taskId", task.getId());
                    response.put("taskName", task.getName());
                    return response;
                })
                .toList();
    }

    @GetMapping("/activeTasks/{role}")
    public List<Map<String, Object>> getTasks(
            @PathVariable String role) {

        return taskService.createTaskQuery()
                .taskCandidateGroup(role)
                .active()
                .list()
                .stream()
                .map(task -> {

                    Map<String, Object> response =
                            new HashMap<>();

                    response.put(
                            "taskId",
                            task.getId());

                    response.put(
                            "taskName",
                            task.getName());

                    return response;
                })
                .toList();
    }
}

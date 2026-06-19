package com.nec.middleware.workflow.controller;

import com.nec.middleware.workflow.dto.request.WorkflowActionRequestDto;
import com.nec.middleware.workflow.dto.response.WorkflowInboxDto;
import com.nec.middleware.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workflow")
public class WorkflowController {

    private final TaskService taskService;

    private final WorkflowService workflowService;


    @GetMapping("/inbox/{role}")
    public ResponseEntity<Page<WorkflowInboxDto>> getInbox(
            @PathVariable String role,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                workflowService.getInbox(role, page, size));
    }

    @GetMapping("/view/{moduleName}/{entityId}")
    public ResponseEntity<Object> viewWorkflow(@PathVariable String moduleName, @PathVariable String entityId) {

        return ResponseEntity.ok(workflowService.getWorkflowDetails(moduleName, entityId));
    }

    @PostMapping("/action")
    public ResponseEntity<String> workflowAction(
            @RequestBody WorkflowActionRequestDto workflowActionRequestDto) {

        workflowService.workflowAction(workflowActionRequestDto);

        return ResponseEntity.ok(
                "Action completed successfully");
    }

    @GetMapping("/rejectedItems")
    public ResponseEntity<Page<WorkflowInboxDto>> getRejectedByRole(@RequestParam String role, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                workflowService.getRejectedWorkflowsByRequesterRole(role, page, size));
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

                    Map<String, Object> response = new HashMap<>();

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

}

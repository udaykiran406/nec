package com.nec.middleware.workflow.controller;

import com.nec.middleware.workflow.dto.request.WorkflowActionRequestDto;
import com.nec.middleware.workflow.dto.response.WorkflowInboxDto;
import com.nec.middleware.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Tag(
        name = "Workflow Management",
        description = "APIs for managing workflows, tasks, and inbox operations")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/workflow")
public class WorkflowController {

    private final TaskService taskService;

    private final WorkflowService workflowService;


    @Operation(summary = "Get Active Tasks",
            description = "Retrieves a list of active tasks in the workflow engine. Each task includes its ID and name.")
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


    @Operation(summary = "Get Workflow Inbox",
            description = "Retrieves a paginated list of workflow inbox items for a specific user role. " +
                    "Each inbox item includes details about the workflow task assigned to that role.")
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

    @Operation(summary = "View Workflow Details",
            description = "Retrieves detailed information about a specific workflow instance based on the provided module name and entity ID. " +
                    "This API is used to view the current state and history of a workflow related to a particular business entity.")
    @GetMapping("/view/{moduleName}/{entityId}")
    public ResponseEntity<Object> viewWorkflow(@PathVariable String moduleName, @PathVariable String entityId) {

        return ResponseEntity.ok(workflowService.getWorkflowDetails(moduleName, entityId));
    }

    @Operation(summary = "Perform Workflow Action",
            description = "Performs a workflow action based on the provided details in the request body. " +
                    "This API is used to execute actions such as approving, rejecting, or completing tasks within a workflow instance.")
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

   @Operation(summary = "Get Active Tasks by Role",
            description = "Retrieves a list of active tasks assigned to a specific user role in the workflow engine. " +
                    "Each task includes its ID and name, allowing users to see tasks relevant to their role.")
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

}

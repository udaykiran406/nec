package com.nec.middleware.workflow.service;

import com.nec.middleware.constants.Constants;
import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.repository.TemporaryContractRepository;
import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import com.nec.middleware.masterdata.repository.ApprovalWorkflowLevelRepository;
import com.nec.middleware.workflow.Enum.WorkflowAction;
import com.nec.middleware.workflow.dto.request.WorkflowActionRequestDto;
import com.nec.middleware.workflow.dto.response.ApprovalLevelStatusDto;
import com.nec.middleware.workflow.dto.response.WorkflowInboxDto;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import com.nec.middleware.workflow.factory.WorkflowModuleFactory;
import com.nec.middleware.workflow.factory.WorkflowModuleHandler;
import com.nec.middleware.workflow.repository.WorkflowAuditRepository;
import com.nec.middleware.workflow.specification.WorkflowInboxSpecification;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    private final TemporaryContractRepository temporaryContractRepository;

    private final ApprovalWorkflowLevelRepository approvalWorkflowLevelRepository;

    private final  WorkflowAuditRepository workflowAuditRepository;

    private final WorkflowModuleFactory workflowModuleFactory;

    private static final String loggedInUser="admin";

    public String startApprovalWorkflow(ApprovalWorkflowLevel approverLevel, String entityId, String moduleName, String requestedBy) {

        Map<String, Object> variables = new HashMap<>();

        variables.put("currentApprovalLevel",approverLevel.getLevelOrder());
        variables.put("approvalRole",approverLevel.getApprovalRole());
        variables.put("currentApproval",approverLevel.getApprovalRole());
        variables.put("moduleName",moduleName);
        variables.put("entityId",entityId);
        variables.put("requestBy",requestedBy);

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(
                        "approvalWorkflow",
                        variables);

        return processInstance.getProcessInstanceId();
    }

    public void createWorkflowAuditRecords(String entityId, String moduleName, String processInstanceId, String createdBy) {

        List<ApprovalWorkflowLevel> levels = approvalWorkflowLevelRepository.findByModuleNameOrderByLevelOrder(moduleName);

        for (ApprovalWorkflowLevel level : levels) {

            WorkflowAudit audit =
                    WorkflowAudit.builder()
                            .moduleName(moduleName)
                            .entityId(entityId)
                            .processInstanceId(processInstanceId)
                            .approvalLevel(level.getLevelOrder())
                            .approvalRole(level.getApprovalRole())
                            .requestedBy(createdBy)
                            .action(
                                    level.getLevelOrder() == 1
                                            ? Constants.WORKFLOW_PENDING_STATUS
                                            : Constants.WORKFLOW_NOT_STARTED_STATUS)
                            .build();

            workflowAuditRepository.save(audit);
        }
    }

    @Transactional(readOnly = true)
    public Object getWorkflowDetails(String moduleName,String entityId) {

        WorkflowModuleHandler handler =workflowModuleFactory.getHandler(moduleName);

        if (handler == null) {
            throw new ResourceNotFoundException(
                    "Workflow handler not found");
        }

        return handler.getDetails(entityId);
    }

    @Transactional
    public void workflowAction(WorkflowActionRequestDto workflowActionRequestDto) {

        Task task =
                taskService.createTaskQuery()
                        .taskId(workflowActionRequestDto.getTaskId())
                        .singleResult();
        String entityId =
                (String) runtimeService.getVariable(
                        task.getExecutionId(),
                        "entityId");

        String moduleName =
                (String) runtimeService.getVariable(
                        task.getExecutionId(),
                        "moduleName");

        Integer currentLevel =
                (Integer) runtimeService.getVariable(
                        task.getExecutionId(),
                        "currentApprovalLevel");

        if (task == null) {
            throw new ResourceNotFoundException(
                    "Task not found");
        }

        Map<String, Object> variables =
                new HashMap<>();

        boolean approved =
                workflowActionRequestDto.getAction() ==
                        WorkflowAction.APPROVE;

        variables.put("approved",approved);

//        variables.put(
//                "actionBy",
//                SecurityContextHolder
//                        .getContext()
//                        .getAuthentication()
//                        .getName());
        variables.put("actionBy",loggedInUser);
        updateAuditEntity(moduleName,entityId,currentLevel,workflowActionRequestDto.getRemarks(),loggedInUser);
        taskService.complete(
                task.getId(),
                variables);
    }
    @Transactional(readOnly = true)
    public Page<WorkflowInboxDto> getInbox( String role,int page,int size) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"));

        Page<WorkflowAudit> auditPage =
                workflowAuditRepository.findAll(
                        WorkflowInboxSpecification
                                .buildSpecification(role),
                        pageable);

        List<String> processInstanceIds =
                auditPage.getContent()
                        .stream()
                        .map(WorkflowAudit::getProcessInstanceId)
                        .toList();

        Map<String, String> taskMap = new HashMap<>();

        if (!processInstanceIds.isEmpty()) {

            List<Task> tasks =
                    taskService.createTaskQuery()
                            .processInstanceIdIn(processInstanceIds)
                            .active()
                            .list();

            taskMap =
                    tasks.stream()
                            .collect(Collectors.toMap(
                                    Task::getProcessInstanceId,
                                    Task::getId));
        } else {
            taskMap = Collections.emptyMap();
        }

        Map<String, String> finalTaskMap = taskMap;
        return auditPage.map(audit ->
                buildInboxDto(
                        audit,
                        finalTaskMap.get(
                                audit.getProcessInstanceId())));
    }


    public void updateAuditEntity(String moduleName,String entityId,Integer currentLevel, String remarks,String loggedInUser){
        List<WorkflowAudit> currentAudit =
                workflowAuditRepository
                        .findByModuleNameAndEntityId(
                                moduleName,
                                entityId);
        WorkflowAudit currentLevelRecord =
                currentAudit.stream()
                        .filter(a ->
                                a.getApprovalLevel()
                                        .equals(currentLevel))
                        .findFirst()
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Current approval level not found"));
        currentLevelRecord.setAction(Constants.WORKFLOW_APPROVED_STATUS);
        currentLevelRecord.setRemarks(remarks);
        currentLevelRecord.setActionBy(loggedInUser);
        currentLevelRecord.setActionDate(LocalDateTime.now());
        currentAudit.stream().filter(a ->
                        a.getApprovalLevel()
                                .equals(currentLevel + 1))
                .findFirst()
                .ifPresent(a ->
                        a.setAction(Constants.WORKFLOW_PENDING_STATUS));

        workflowAuditRepository.saveAll(currentAudit);
    }


    private WorkflowInboxDto buildInboxDto(WorkflowAudit audit,String taskId) {

        return WorkflowInboxDto.builder()
                .createdDateTime(
                        audit.getCreatedAt())
                .taskId(taskId)
                .entityId(
                        audit.getEntityId())
                .moduleName(
                        audit.getModuleName())
                .requestedBy(audit.getRequestedBy())
                .processInstanceId(
                        audit.getProcessInstanceId())
                .status(audit.getAction())
                .currentApprovalRole(
                        audit.getApprovalRole())
                .build();
    }

//    public void processApproval(WorkflowRequestDto request) {
//
//        Map<String, Object> variables = new HashMap<>();
//
//        variables.put("approved",request.getApproved());
//
//        taskService.complete(request.getTaskId(),variables);
//    }

    public void approveTask(String taskId) {

        taskService.complete(taskId);
    }

    public long activeWorkFlowCount() {
        return runtimeService
                .createProcessInstanceQuery()
                .active()
                .count();
    }
}
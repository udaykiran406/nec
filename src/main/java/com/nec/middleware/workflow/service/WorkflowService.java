package com.nec.middleware.workflow.service;

import com.nec.middleware.constants.Constants;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import com.nec.middleware.masterdata.repository.ApprovalWorkflowLevelRepository;
import com.nec.middleware.workflow.Enum.WorkflowAction;
import com.nec.middleware.workflow.dto.request.WorkflowActionRequestDto;
import com.nec.middleware.workflow.dto.response.WorkflowInboxDto;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import com.nec.middleware.workflow.entity.WorkflowSlaConfiguration;
import com.nec.middleware.workflow.factory.WorkflowEntityFactory;
import com.nec.middleware.workflow.factory.WorkflowEntityService;
import com.nec.middleware.workflow.factory.WorkflowModuleFactory;
import com.nec.middleware.workflow.factory.WorkflowModuleHandler;
import com.nec.middleware.workflow.repository.WorkflowAuditRepository;
import com.nec.middleware.workflow.repository.WorkflowSlaConfigurationRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    private final WorkflowSlaConfigurationRepository slaConfigurationRepository;

    private final ApprovalWorkflowLevelRepository approvalWorkflowLevelRepository;

    private final WorkflowAuditRepository workflowAuditRepository;

    private final WorkflowModuleFactory workflowModuleFactory;
    private final WorkflowEntityFactory workflowEntityFactory;
    private static final String loggedInUser = "admin";

    public String startApprovalWorkflow(ApprovalWorkflowLevel approverLevel, String entityId, String moduleName, String requestedBy, String requesterRole) {

        WorkflowSlaConfiguration sla = slaConfigurationRepository.findByModuleName(moduleName)
                .orElseThrow(() -> new ResourceNotFoundException("SLA configuration not found once the workflow is started for module name --> " + moduleName + " entity id --> " + entityId));
        Map<String, Object> variables = new HashMap<>();
        variables.put("currentApprovalLevel", approverLevel.getLevelOrder());
        variables.put("approvalRole", approverLevel.getApprovalRole());
        variables.put("currentApproval", approverLevel.getApprovalRole());
        variables.put("moduleName", moduleName);
        variables.put("entityId", entityId);
        variables.put("requestedBy", requestedBy);
        variables.put("requestedRole", requesterRole);
        variables.put("reminderDuration", sla.getReminderHours());
        variables.put("secondReminderDuration", sla.getSecondReminderHours());
        variables.put("escalationDuration", sla.getEscalationHours());
//        variables.put("escalationDuration", "PT" + sla.getEscalationHours() + "H");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("approvalWorkflow", variables);
        return processInstance.getProcessInstanceId();
    }

    public void createWorkflowAuditRecords(String entityId, String moduleName, String processInstanceId, String createdBy, String requesterRole) {

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
                            .requesterRole(requesterRole)
                            .revisionNo(1)
                            .action(
                                    level.getLevelOrder() == 1
                                            ? Constants.WORKFLOW_PENDING_STATUS
                                            : Constants.WORKFLOW_NOT_STARTED_STATUS)
                            .build();

            workflowAuditRepository.save(audit);
        }
    }

    @Transactional(readOnly = true)
    public Object getWorkflowDetails(String moduleName, String entityId) {

        WorkflowModuleHandler handler = workflowModuleFactory.getHandler(moduleName);

        if (handler == null) {
            throw new ResourceNotFoundException("Workflow handler not found");
        }

        return handler.getDetails(entityId);
    }

    @Transactional
    public void workflowAction(WorkflowActionRequestDto workflowActionRequestDto) {

        Task task =
                taskService.createTaskQuery()
                        .taskId(workflowActionRequestDto.getTaskId())
                        .singleResult();
        if (task == null) {
            throw new ResourceNotFoundException(
                    "Task not found");
        }
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

        Map<String, Object> variables = new HashMap<>();
        boolean approved =
                workflowActionRequestDto.getAction() ==
                        WorkflowAction.APPROVE;

        variables.put("approved", approved);
        variables.put("actionBy", loggedInUser);
        variables.put("remarks", workflowActionRequestDto.getRemarks());

        if (approved) {
            updateApprovedAudit(moduleName, entityId, currentLevel,
                    workflowActionRequestDto.getRemarks(), loggedInUser);

        } else if (workflowActionRequestDto.getAction() ==
                WorkflowAction.REJECT) {
            updateRejectedAudit(moduleName, entityId, currentLevel,
                    workflowActionRequestDto.getRemarks(),
                    loggedInUser);
        } else {

            variables.put("resubmitted", workflowActionRequestDto.getAction() ==
                    WorkflowAction.RESUBMIT);
        }
        taskService.complete(task.getId(), variables);
    }

    @Transactional(readOnly = true)
    public Page<WorkflowInboxDto> getRejectedWorkflowsByRequesterRole(String role, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<WorkflowAudit> auditPage =
                workflowAuditRepository
                        .findRejectedForRequesterRole(
                                role,
                                pageable);

        List<String> processInstanceIds =
                auditPage.getContent()
                        .stream()
                        .map(WorkflowAudit::getProcessInstanceId)
                        .distinct()
                        .toList();

        Map<String, String> taskMap;

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
                                    Task::getId,
                                    (existing, replacement) -> existing));
        } else {

            taskMap = Collections.emptyMap();
        }

        Map<String, String> finalTaskMap = taskMap;

        return auditPage.map(audit ->
                buildRejectedDto(
                        audit,
                        finalTaskMap.get(
                                audit.getProcessInstanceId())));
    }

    @Transactional(readOnly = true)
    public Page<WorkflowInboxDto> getInbox(String role, int page, int size) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(
                        Sort.Direction.ASC,
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

            taskMap = tasks.stream().collect(Collectors.toMap(Task::getProcessInstanceId, Task::getId));
        } else {
            taskMap = Collections.emptyMap();
        }
        Map<String, String> finalTaskMap = taskMap;
        return auditPage.map(audit -> buildInboxDto(audit, finalTaskMap.get(audit.getProcessInstanceId())));
    }

    @Transactional
    public void updateApprovedAudit(String moduleName, String entityId, Integer currentLevel, String remarks, String loggedInUser) {
        List<WorkflowAudit> currentAudit =
                workflowAuditRepository.findLatestRevisionAudits(moduleName, entityId);
        WorkflowAudit currentLevelRecord =
                currentAudit.stream()
                        .filter(a ->
                                a.getApprovalLevel()
                                        .equals(currentLevel))
                        .findFirst()
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Current approval level not found for entityID --> " + entityId + " module name --> " + moduleName));
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

    @Transactional
    public void updateRejectedAudit(String moduleName, String entityId, Integer currentLevel, String remarks, String loggedInUser) {

        List<WorkflowAudit> audits =
                workflowAuditRepository.findLatestRevisionAudits(moduleName, entityId);
        WorkflowAudit current = audits.stream().filter(a -> a.getApprovalLevel().equals(currentLevel)).findFirst().orElseThrow();
        current.setAction(
                Constants.WORKFLOW_REJECTED_STATUS);
        current.setRemarks(remarks);
        current.setActionBy(loggedInUser);
        current.setActionDate(LocalDateTime.now());
        workflowAuditRepository.save(current);
        WorkflowEntityService entityService =
                workflowEntityFactory.getService(
                        moduleName);
        entityService.markRejected(entityId, remarks);
    }

    private WorkflowInboxDto buildInboxDto(WorkflowAudit audit, String taskId) {

        return WorkflowInboxDto.builder()
                .createdDateTime(audit.getCreatedAt())
                .taskId(taskId)
                .entityId(audit.getEntityId())
                .moduleName(audit.getModuleName())
                .requestedBy(audit.getRequestedBy())
                .processInstanceId(audit.getProcessInstanceId())
                .status(audit.getAction())
                .currentApprovalRole(audit.getApprovalRole())
                .build();
    }

    private WorkflowInboxDto buildRejectedDto(WorkflowAudit audit, String taskId) {

        return WorkflowInboxDto.builder()
                .entityId(audit.getEntityId())
                .taskId(taskId)
                .moduleName(audit.getModuleName())
                .processInstanceId(audit.getProcessInstanceId())
                .requestedBy(audit.getRequestedBy())
                .status(audit.getAction())
                .currentApprovalRole(audit.getApprovalRole())
                .createdDateTime(audit.getCreatedAt())
                .build();
    }

    public void approveTask(String taskId) {

        taskService.complete(taskId);
    }

    public long activeWorkFlowCount() {
        return runtimeService.createProcessInstanceQuery().active().count();
    }
}
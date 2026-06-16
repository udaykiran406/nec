package com.nec.middleware.workflow.service;

import com.nec.middleware.hr.dto.response.TemporaryContractResponseDto;
import com.nec.middleware.hr.entity.TemporaryContract;
import com.nec.middleware.hr.repository.TemporaryContractRepository;
import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import com.nec.middleware.masterdata.repository.ApprovalWorkflowLevelRepository;
import com.nec.middleware.workflow.dto.request.WorkflowRequestDto;
import com.nec.middleware.workflow.dto.response.ApprovalLevelStatusDto;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import com.nec.middleware.workflow.repository.WorkflowAuditRepository;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

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

    ApprovalWorkflowLevelRepository approvalWorkflowLevelRepository;

    WorkflowAuditRepository workflowAuditRepository;

    public String startApprovalWorkflow(ApprovalWorkflowLevel approverLevel, String entityId, String moduleName, String requestedBy) {

        Map<String, Object> variables = new HashMap<>();

        variables.put("currentApprovalLevel",approverLevel.getLevelOrder());
//        variables.put("offeredTo",approverLevel.getApprovalRole());
        variables.put("moduleName",moduleName);
        variables.put("entityId",entityId);
        variables.put("requestBy",requestedBy);

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(
                        "approvalWorkflow",
                        variables);

        return processInstance.getProcessInstanceId();
    }

    public void createWorkflowAuditRecords(String entityId, String moduleName, String processInstanceId) {

        List<ApprovalWorkflowLevel> levels = approvalWorkflowLevelRepository.findByModuleNameOrderByLevelOrder(moduleName);

        for (ApprovalWorkflowLevel level : levels) {

            WorkflowAudit audit =
                    WorkflowAudit.builder()
                            .moduleName(moduleName)
                            .entityId(entityId)
                            .processInstanceId(processInstanceId)
                            .approvalLevel(level.getLevelOrder())
                            .approvalRole(level.getApprovalRole())
                            .action(
                                    level.getLevelOrder() == 1
                                            ? "PENDING"
                                            : "NOT_STARTED")
                            .build();

            workflowAuditRepository.save(audit);
        }
    }

    public List<TemporaryContractResponseDto> getInbox(String role) {

        List<Task> tasks =
                taskService.createTaskQuery()
                        .taskCandidateGroup(role)
                        .active()
                        .list();

        return tasks.stream()
                .map(task -> {

                    Long contractPk =
                            (Long) runtimeService.getVariable(
                                    task.getProcessInstanceId(),
                                    "contractId");

                    TemporaryContract contract =
                            temporaryContractRepository
                                    .findById(contractPk)
                                    .orElseThrow();

                    return TemporaryContractResponseDto.builder()
                            .taskId(task.getId())
                            .taskName(task.getName())
                            .contractId(contract.getContractId())
                            .employeeName(contract.getEmployeeName())
                            .totalContractAmount(contract.getTotalContractAmount())
                            .build();
                })
                .toList();
    }


    public List<ApprovalLevelStatusDto> buildApprovalHistory(String moduleName, String entityId) {

        List<ApprovalWorkflowLevel> approvalLevels =
                approvalWorkflowLevelRepository
                        .findByModuleNameOrderByLevelOrder(
                                moduleName);

        List<WorkflowAudit> auditRecords =
                workflowAuditRepository
                        .findByModuleNameAndEntityIdOrderByApprovalLevel(
                                moduleName,
                                entityId);

        Map<Integer, WorkflowAudit> auditMap =
                auditRecords.stream()
                        .collect(Collectors.toMap(
                                WorkflowAudit::getApprovalLevel,
                                Function.identity()));

        return approvalLevels.stream()
                .map(level -> {
                    WorkflowAudit audit =
                            auditMap.get(level.getLevelOrder());

                    if (audit != null) {

                        return ApprovalLevelStatusDto.builder()
                                .approvalLevel(
                                        level.getLevelOrder())
                                .approvalRole(
                                        level.getApprovalRole())
                                .status(
                                        audit.getAction())
                                .actionBy(
                                        audit.getActionBy())
                                .actionDate(
                                        audit.getActionDate())
                                .remarks(
                                        audit.getRemarks())
                                .build();
                    }

                    return ApprovalLevelStatusDto.builder()
                            .approvalLevel(
                                    level.getLevelOrder())
                            .approvalRole(
                                    level.getApprovalRole())
                            .status("PENDING")
                            .build();
                })
                .toList();
    }

    public void processApproval(WorkflowRequestDto request) {

        Map<String, Object> variables = new HashMap<>();

        variables.put("approved",request.getApproved());

        taskService.complete(request.getTaskId(),variables);
    }

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
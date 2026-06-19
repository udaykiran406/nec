package com.nec.middleware.workflow.service;

import com.nec.middleware.constants.Constants;
import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import com.nec.middleware.masterdata.repository.ApprovalWorkflowLevelRepository;
import com.nec.middleware.workflow.dto.response.ApprovalLevelStatusDto;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import com.nec.middleware.workflow.repository.WorkflowAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowAuditService {
    private final ApprovalWorkflowLevelRepository approvalWorkflowLevelRepository;
    private final WorkflowAuditRepository workflowAuditRepository;

    public List<ApprovalLevelStatusDto> buildApprovalHistory(String moduleName, String entityId) {

        List<ApprovalWorkflowLevel> approvalLevels =
                approvalWorkflowLevelRepository
                        .findByModuleNameOrderByLevelOrder(
                                moduleName);

        List<WorkflowAudit> auditRecords =
                workflowAuditRepository
                        .findLatestRevisionAudits(
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
                                .createdDate(
                                        audit.getCreatedAt())
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
                            .status(
                                    Constants.WORKFLOW_NOT_STARTED_STATUS)
                            .build();
                })
                .toList();
    }
}

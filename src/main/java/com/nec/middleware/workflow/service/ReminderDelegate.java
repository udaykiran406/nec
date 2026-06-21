package com.nec.middleware.workflow.service;

import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.workflow.entity.User;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import com.nec.middleware.workflow.repository.UserRepository;
import com.nec.middleware.workflow.repository.WorkflowAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("reminderDelegate")
@RequiredArgsConstructor
public class ReminderDelegate implements JavaDelegate {

    private final WorkflowAuditRepository auditRepository;

    private final EmailService emailService;

    private final UserRepository userRepository;

    @Override
    public void execute(
            DelegateExecution execution) {
        String moduleName =
                (String) execution.getVariable(
                        "moduleName");
        String entityId =
                (String) execution.getVariable(
                        "entityId");

        String role =
                (String) execution.getVariable(
                        "approvalRole");

        // Data for testing purpose
        List<User> users =
                userRepository.findByRole(role);
        log.info("User details ---> {} , entity id {}", users.get(0).getEmail(), entityId);
        users.forEach(user ->
                emailService.sendEmail(
                        user.getEmail(),
                        "Workflow Reminder",
                        "Workflow "
                                + entityId
                                + " is pending approval"));

        WorkflowAudit audit = auditRepository.findCurrentPendingAudit(moduleName, entityId, role)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Module not found in workflow Audit table to send reminder for approval with pending status for entityId -->" + entityId + " and module --> " + moduleName));

        audit.setReminderCount(
                Optional.ofNullable(
                                audit.getReminderCount())
                        .orElse(0) + 1);

        audit.setLastReminderDate(LocalDateTime.now());

        auditRepository.save(audit);
    }
}

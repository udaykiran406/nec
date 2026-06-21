package com.nec.middleware.workflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "workflow_audit",
        indexes = {
                // Latest revision lookup
                @Index(
                        name = "idx_audit_module_entity_revision",
                        columnList = "module_name,entity_id,revision_no"
                ),
                // Inbox queries
                @Index(
                        name = "idx_audit_role_action_revision",
                        columnList = "approval_role,action,revision_no"
                ),
                // Rejected workflows
                @Index(
                        name = "idx_audit_requester_role_action",
                        columnList = "requested_role,action"
                ),
                // Escalation scheduler
                @Index(
                        name = "idx_audit_action_created",
                        columnList = "action,created_at"
                ),
                // Process instance lookup
                @Index(
                        name = "idx_audit_process_instance",
                        columnList = "process_instance_id"
                )
        }
)
public class WorkflowAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "process_instance_id")
    private String processInstanceId;

    @Column(name = "approval_level")
    private Integer approvalLevel;

    @Column(name = "approval_role")
    private String approvalRole;

    @Column(name = "action")
    private String action;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "action_by")
    private String actionBy;
    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "requested_role")
    private String requesterRole;
    @Column(name = "action_date")
    private LocalDateTime actionDate;
    @Column(name = "revision_no")
    private Integer revisionNo;
    @Column(name = "reminder_count")
    private Integer reminderCount;

    @Column(name = "last_reminder_date")
    private LocalDateTime lastReminderDate;

    @Column(name = "escalated")
    private Boolean escalated;

    @Column(name = "escalated_to")
    private String escalatedTo;

    @Column(name = "escalated_date")
    private LocalDateTime escalatedDate;
}

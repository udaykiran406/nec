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
        schema = "NEC",
        indexes = {
                @Index(
                        name = "idx_module_entity",
                        columnList = "module_name, entity_id"
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
}

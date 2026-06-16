package com.nec.middleware.workflow.entity;

import com.nec.middleware.hr.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowAudit extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "action_date")
    private LocalDateTime actionDate;
}

package com.nec.middleware.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workflow_escalation_matrix", indexes = {
        @Index(
                name = "idx_escalation_module_role",
                columnList = "module_name,approval_role"
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowEscalationMatrix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_name")
    private String moduleName;

    @Column(name = "approval_role")
    private String approvalRole;

    @Column(name = "escalation_role")
    private String escalationRole;
}
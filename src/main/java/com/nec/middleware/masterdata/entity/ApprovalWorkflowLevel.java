package com.nec.middleware.masterdata.entity;

import com.nec.middleware.idGenerator.Enum.ModuleCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "approval_workflow_level")
@Getter
@Setter
public class ApprovalWorkflowLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @Column(name = "level_order", nullable = false)
    private Integer levelOrder;

    @Column(name = "approval_role", nullable = false)
    private String approvalRole;
}

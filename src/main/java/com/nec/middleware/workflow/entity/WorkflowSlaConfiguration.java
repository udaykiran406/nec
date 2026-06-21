package com.nec.middleware.workflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workflow_sla_configuration", indexes = {
        @Index(
                name = "idx_escalation_config_module",
                columnList = "module_name"
        )
})
@Getter
@Setter
public class WorkflowSlaConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String moduleName;

    private String reminderHours;

    private String secondReminderHours;

    private String escalationHours;

    private Boolean active;
}
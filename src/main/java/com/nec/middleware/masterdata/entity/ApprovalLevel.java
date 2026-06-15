package com.nec.middleware.masterdata.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "approval_levels")
public class ApprovalLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @Column(name = "level_order", nullable = false)
    private Integer levelOrder;

    @Column(name = "level_name", nullable = false)
    private String levelName;
}

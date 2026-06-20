package com.nec.middleware.rbacAuth.rbac.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "nec_rbac_modules",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_nec_rbac_module_code", columnNames = "module_code")
    },
    indexes = {
        @Index(name = "idx_nec_rbac_module_status", columnList = "status"),
        @Index(name = "idx_nec_rbac_module_deleted", columnList = "is_deleted"),
        @Index(name = "idx_nec_rbac_module_code", columnList = "module_code"),
        @Index(name = "idx_nec_rbac_module_display_order", columnList = "display_order")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacModule extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "module_code", nullable = false, length = 50, unique = true)
    private String moduleCode;

    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Integer isDeleted = 0;
}

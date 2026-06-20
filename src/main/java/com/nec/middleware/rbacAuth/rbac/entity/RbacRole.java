package com.nec.middleware.rbacAuth.rbac.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(
    name = "nec_rbac_roles",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_nec_rbac_roles_code", columnNames = "role_code"),
        @UniqueConstraint(name = "uq_nec_rbac_roles_name", columnNames = "role_name")
    },
    indexes = {
        @Index(name = "idx_nec_rbac_roles_status", columnList = "status"),
        @Index(name = "idx_nec_rbac_roles_deleted", columnList = "is_deleted"),
        @Index(name = "idx_nec_rbac_roles_parent", columnList = "parent_role_id"),
        @Index(name = "idx_nec_rbac_roles_code", columnList = "role_code"),
        @Index(name = "idx_nec_rbac_roles_name", columnList = "role_name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacRole extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "role_code", nullable = false, length = 50, unique = true)
    private String roleCode;

    @Column(name = "role_name", nullable = false, length = 100, unique = true)
    private String roleName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "parent_role_id")
    private Long parentRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_role_id", referencedColumnName = "role_id", insertable = false, updatable = false)
    private RbacRole parentRole;

    @Column(name = "approval_limit", precision = 18, scale = 2)
    private BigDecimal approvalLimit;

    @Column(name = "is_parent_role", nullable = false)
    @Builder.Default
    private Boolean isParentRole = false;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Integer isDeleted = 0;
}

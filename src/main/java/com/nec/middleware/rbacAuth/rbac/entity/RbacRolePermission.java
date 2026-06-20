package com.nec.middleware.rbacAuth.rbac.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class representing the {@code rbac_role_permission} table.
 * Maps roles to permissions through modules and groups.
 *
 * <p>Audit fields inherited from {@link BaseAuditEntity}.
 */
@Entity
@Table(
    name = "nec_rbac_role_permission",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_rbac_role_permission",
            columnNames = {"role_id", "module_id", "group_id", "permission_id"})
    },
    indexes = {
        @Index(name = "idx_rbac_role_permission_role", columnList = "role_id"),
        @Index(name = "idx_rbac_role_permission_module", columnList = "module_id"),
        @Index(name = "idx_rbac_role_permission_group", columnList = "group_id"),
        @Index(name = "idx_rbac_role_permission_permission", columnList = "permission_id"),
        @Index(name = "idx_rbac_role_permission_status", columnList = "status"),
        @Index(name = "idx_rbac_role_permission_composite", columnList = "role_id,module_id,group_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacRolePermission extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_permission_id", nullable = false)
    private Long rolePermissionId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RbacRole role;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", insertable = false, updatable = false)
    private RbacModule module;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private RbacPermissionGroup group;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    private RbacPermission permission;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";
}

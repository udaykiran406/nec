package com.nec.middleware.rbacAuth.rbac.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class representing the {@code rbac_permission} table.
 * Manages permission definitions with module and group references.
 *
 * <p>Audit fields inherited from {@link BaseAuditEntity}.
 */
@Entity
@Table(
    name = "nec_rbac_permissions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_rbac_permission_code", columnNames = {"module_id", "permission_code"})
    },
    indexes = {
        @Index(name = "idx_rbac_permission_module", columnList = "module_id"),
        @Index(name = "idx_rbac_permission_group", columnList = "group_id"),
        @Index(name = "idx_rbac_permission_status", columnList = "status"),
        @Index(name = "idx_rbac_permission_code", columnList = "permission_code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacPermission extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

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

    @Column(name = "permission_code", nullable = false, length = 50)
    private String permissionCode;

    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "is_side_menu", nullable = false)
    @Builder.Default
    private Boolean isSideMenu = false;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Integer isDeleted = 0;
}

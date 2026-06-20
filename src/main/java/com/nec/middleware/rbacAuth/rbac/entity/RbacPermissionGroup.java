package com.nec.middleware.rbacAuth.rbac.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class representing the {@code rbac_permission_group} table.
 * Manages permission group definitions with module references.
 *
 * <p>Audit fields inherited from {@link BaseAuditEntity}.
 */
@Entity
@Table(
    name = "nec_rbac_groups",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_rbac_group_code", columnNames = {"module_id", "group_code"})
    },
    indexes = {
        @Index(name = "idx_rbac_group_module", columnList = "module_id"),
        @Index(name = "idx_rbac_group_status", columnList = "status"),
        @Index(name = "idx_rbac_group_code", columnList = "group_code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacPermissionGroup extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", insertable = false, updatable = false)
    private RbacModule module;

    @Column(name = "group_code", nullable = false, length = 50)
    private String groupCode;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

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

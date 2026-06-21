package com.nec.middleware.rbacAuth.rbac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing the {@code rbac_role_permission} table.
 * Maps roles to permissions through modules and groups.
 * Each record represents a single permission assigned to a role within a specific module and group.
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
public class RbacRolePermission {

    // ==================== PRIMARY KEY ====================
    /**
     * Primary key – role permission mapping ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_permission_id", nullable = false)
    private Long rolePermissionId;

    // ==================== BUSINESS FIELDS ====================
    /**
     * FK → nec_rbac_roles.role_id
     */
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * Role association for filtering and response enrichment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RbacRole role;

    /**
     * FK → nec_rbac_module.module_id
     */
    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    /**
     * Module association for filtering and response enrichment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", insertable = false, updatable = false)
    private RbacModule module;

    /**
     * FK → rbac_permission_group.group_id
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /**
     * Permission group association for filtering and response enrichment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private RbacPermissionGroup group;

    /**
     * FK → rbac_permission.permission_id
     */
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    /**
     * Permission association for filtering and response enrichment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    private RbacPermission permission;

    // ==================== STATUS / FLAGS ====================
    /**
     * Status of the mapping: 'ACTIVE' or 'INACTIVE'.
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    // ==================== AUDIT FIELDS ====================
    /**
     * ID of the user who created this record.
     */
    @Column(name = "created_by", length = 100)
    private String createdByUserId;

    /**
     * Timestamp automatically set on INSERT.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * ID of the user who last updated this record.
     */
    @Column(name = "updated_by", length = 100)
    private String modifiedByUserId;

    /**
     * Timestamp automatically updated on each UPDATE.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime modifiedDate;
}

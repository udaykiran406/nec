package com.nec.middleware.rbacAuth.rbac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing the {@code rbac_permission} table.
 * Manages permission definitions with module and group references.
 */
@Entity
@Table(
    name = "nec_rbac_permission",
    schema = "keycloak",
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
public class RbacPermission {

    // ==================== PRIMARY KEY ====================
    /**
     * Primary key – permission ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    // ==================== BUSINESS FIELDS ====================
    /**
     * FK → rbac_module.module_id
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
     * Unique permission code within module (max 50 chars).
     */
    @Column(name = "permission_code", nullable = false, length = 50)
    private String permissionCode;

    /**
     * Permission name (max 100 chars) – human-readable name.
     */
    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;

    /**
     * Optional description of the permission (max 500 chars).
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Display order for UI presentation (starting from 1).
     */
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    // ==================== STATUS / FLAGS ====================
    /**
     * Status of the permission: 'ACTIVE' or 'INACTIVE'.
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    /**
     * Flag to indicate if permission appears in side menu.
     */
    @Column(name = "is_side_menu", nullable = false)
    @Builder.Default
    private Boolean isSideMenu = false;

    // ==================== SOFT DELETE ====================
    /**
     * Soft-delete flag: false = active, true = soft-deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Integer isDeleted = 0;

    // ==================== AUDIT FIELDS ====================
    /**
     * ID of the user who created this record.
     */
    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    /**
     * Timestamp automatically set on INSERT.
     */
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * ID of the user who last updated this record.
     */
    @Column(name = "modified_by_user_id")
    private Long modifiedByUserId;

    /**
     * Timestamp automatically updated on each UPDATE.
     */
    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
}

package com.nec.middleware.rbacAuth.rbac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing the {@code rbac_permission_group} table.
 * Manages permission group definitions with module references.
 */
@Entity
@Table(
    name = "nec_rbac_permission_group",
    schema = "keycloak",
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
public class RbacPermissionGroup {

    // ==================== PRIMARY KEY ====================
    /**
     * Primary key – group ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id", nullable = false)
    private Long groupId;

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
     * Unique group code within module (max 50 chars).
     */
    @Column(name = "group_code", nullable = false, length = 50)
    private String groupCode;

    /**
     * Group name (max 100 chars) – human-readable name.
     */
    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    /**
     * Optional description of the group (max 500 chars).
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
     * Status of the group: 'ACTIVE' or 'INACTIVE'.
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";
     /**
    // ==================== SOFT DELETE ====================
    /**
     * Soft-delete flag: false = active, true = soft-deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Integer isDeleted = 0;

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

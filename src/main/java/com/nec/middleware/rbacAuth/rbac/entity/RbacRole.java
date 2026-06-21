package com.nec.middleware.rbacAuth.rbac.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing the {@code nec_rbac_roles} table.
 * Manages role definitions with hierarchical support and approval limits.
 */
@Entity
@Table(
    name = "nec_rbac_roles",
    schema = "keycloak",
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
public class RbacRole {

    // ==================== PRIMARY KEY ====================
    /**
     * Primary key – role ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    // ==================== BUSINESS FIELDS ====================
    /**
     * Unique role code (max 50 chars) – used for programmatic reference.
     */
    @Column(name = "role_code", nullable = false, length = 50, unique = true)
    private String roleCode;

    /**
     * Unique role name (max 100 chars) – human-readable name.
     */
    @Column(name = "role_name", nullable = false, length = 100, unique = true)
    private String roleName;

    /**
     * Optional description of the role (max 500 chars).
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * FK → nec_rbac_roles.role_id – parent role ID for hierarchical roles.
     */
    @Column(name = "parent_role_id")
    private Long parentRoleId;

    /**
     * Parent role association for hierarchical queries and filtering.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_role_id", referencedColumnName = "role_id", insertable = false, updatable = false)
    private RbacRole parentRole;

    /**
     * Numeric limit for approval amounts. NULL if no limit.
     */
    @Column(name = "approval_limit", precision = 18, scale = 2)
    private BigDecimal approvalLimit;

    // ==================== STATUS / FLAGS ====================
    /**
     * Flag indicating whether this role is a parent role.
     */
    @Column(name = "is_parent_role", nullable = false)
    @Builder.Default
    private Boolean isParentRole = false;

    /**
     * Status of the role: 'ACTIVE' or 'INACTIVE'.
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

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
     * ID of the user who last updated this record (Keycloak UUID).
     */
    @Column(name = "modified_by_user_id", length = 36)
    private String modifiedByUserId;

    /**
     * Timestamp automatically updated on each UPDATE.
     */
    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    // ==================== SOFT DELETE ====================
    /**
     * Soft-delete flag: false = active, true = soft-deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Integer isDeleted = 0;
}

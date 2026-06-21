package com.nec.middleware.rbacAuth.rbac.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing the {@code nec_rbac_module} table.
 * Manages module definitions with display order and status.
 */
@Entity
@Table(
    name = "nec_rbac_module",
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
public class RbacModule {

    // ==================== PRIMARY KEY ====================
    /**
     * Primary key – module ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    // ==================== BUSINESS FIELDS ====================
    /**
     * Unique module code (max 50 chars) – used for programmatic reference.
     */
    @Column(name = "module_code", nullable = false, length = 50, unique = true)
    private String moduleCode;

    /**
     * Module name (max 100 chars) – human-readable name.
     */
    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;

    /**
     * Optional description of the module (max 500 chars).
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
     * Status of the module: 'ACTIVE' or 'INACTIVE'.
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    // ==================== AUDIT FIELDS ====================
    /**
     * ID of the user who created this record.
     */
    @Column(name = "created_by_user_id")
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

    // ==================== SOFT DELETE ====================
    /**
     * Soft-delete flag: false = active, true = soft-deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Integer isDeleted = 0;
}

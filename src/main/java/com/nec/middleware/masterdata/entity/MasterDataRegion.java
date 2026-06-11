package com.nec.middleware.masterdata.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing the {@code nec_regions} table.
 * Master table for geographic regions of Somaliland.
 */
@Entity
@Table(
    name = "nec_regions",
    uniqueConstraints = @UniqueConstraint(name = "uq_regions_name", columnNames = "region_name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterDataRegion {

    /**
     * Primary key – S.NO in UI.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Unique name of the region (max 150 chars).
     */
    @Column(name = "region_name", nullable = false, length = 150, unique = true)
    private String regionName;

    /**
     * Toggled via refresh icon in UI. Allowed values: 'active' | 'inactive'.
     */
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private String status = "active";

    /**
     * FK → nec_rbac_users.id – the user who created this record.
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * FK → nec_rbac_users.id – the user who last updated this record.
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * Timestamp automatically set on INSERT.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp automatically updated on each UPDATE.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Soft-delete timestamp; NULL = not deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Soft-delete flag: 0 = active, 1 = soft-deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Short isDeleted = 0;
}


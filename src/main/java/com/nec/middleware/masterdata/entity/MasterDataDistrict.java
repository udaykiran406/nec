package com.nec.middleware.masterdata.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing the {@code nec_districts} table.
 * Master table for geographic districts (child of Region).
 */
@Entity
@Table(
    name = "nec_districts",
    uniqueConstraints = @UniqueConstraint(name = "uq_district_name_region", columnNames = {"district_name", "region_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterDataDistrict {

    /**
     * Primary key – S.NO in UI.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * FK → nec_regions.id
     */
    @Column(name = "region_id", nullable = false)
    private Long regionId;

    /**
     * Name of the district (max 150 chars). Unique within a region.
     */
    @Column(name = "district_name", nullable = false, length = 150)
    private String districtName;

    /**
     * Toggled via refresh icon in UI. Allowed values: 'Active' | 'Inactive'.
     */
    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private String status = "Active";

    /**
     * FK → nec_rbac_users.id – the user who created this record.
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * FK → nec_rbac_users.id – the user who last updated this record.
     */
    @Column(name = "updated_by")
    private String updatedBy;

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



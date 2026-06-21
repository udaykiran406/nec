package com.nec.middleware.masterdata.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "nec_universities",
        schema = "NEC",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_university_name",
                columnNames = "university_name"
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterDataUniversity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "university_name", nullable = false, length = 200)
    private String universityName;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "location", nullable = false, length = 200)
    private String location;

    @Column(name = "status", nullable = false, length = 10)
    @Builder.Default
    private String status = "Active";

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Short isDeleted = 0;
}
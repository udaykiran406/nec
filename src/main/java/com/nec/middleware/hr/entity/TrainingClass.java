package com.nec.middleware.hr.entity;

import com.nec.middleware.Lookups.entity.TrainingManagementStatus;
import com.nec.middleware.Lookups.entity.TrainingTypes;
import com.nec.middleware.masterdata.entity.*;
import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: nec_training_classes
 *
 * FK associations:
 *  - trainingTypeId → nec_lkp_training_types.id
 *  - statusId       → nec_lkp_training_management_status.id
 *  - regionId       → nec_regions.id
 *  - districtId     → nec_districts.id
 *  - cityId         → nec_cities.id
 *  - universityId   → nec_universities.id
 *  - trainerTotId   → nec_hr_trainer_tots.id
 *
 * Audit fields inherited from {@link AuditableEntity}.
 */
@Entity
@Table(name = "nec_training_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingClass extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_code", nullable = false, unique = true, length = 20)
    private String classCode;

    @Column(name = "class_name", nullable = false, length = 150)
    private String className;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "location", nullable = false, length = 200)
    private String location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "pre_requests", columnDefinition = "TEXT")
    private String preRequests;

    // ------------------------------------------------------------------ LOOKUP FKs

    /**
     * FK → nec_lkp_training_types.id
     * Stored column: training_type_id
     */
    @Column(name = "training_type_id", nullable = false, insertable = false, updatable = false)
    private Long trainingTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingTypes trainingType;

    /**
     * FK → nec_lkp_training_management_status.id
     * Stored column: status_id
     */
    @Column(name = "status_id", nullable = false, insertable = false, updatable = false)
    private Long statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private TrainingManagementStatus status;

    // ------------------------------------------------------------------ MASTER DATA FKs

    /**
     * FK → nec_regions.id
     * Stored column: region_id
     */
    @Column(name = "region_id", nullable = false, insertable = false, updatable = false)
    private Long regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private MasterDataRegion region;

    /**
     * FK → nec_districts.id
     * Stored column: district_id
     */
    @Column(name = "district_id", nullable = false, insertable = false, updatable = false)
    private Long districtId;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private MasterDataDistrict district;

    /**
     * FK → nec_cities.id
     * Stored column: city_id
     */
    @Column(name = "city_id", nullable = false, insertable = false, updatable = false)
    private Long cityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private MasterDataCity city;

    /**
     * FK → nec_universities.id
     * Stored column: university_id
     */
    @Column(name = "university_id", nullable = false, insertable = false, updatable = false)
    private Long universityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private MasterDataUniversity university;

    /**
     * FK → nec_hr_trainer_tots.id
     * Stored column: trainer_tot_id
     */
    @Column(name = "trainer_tot_id", nullable = false, insertable = false, updatable = false)
    private Long trainerTotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_tot_id", nullable = false)
    private MasterDataHrTrainerTot trainerTot;
}
package com.nec.middleware.hr.entity;

import com.nec.middleware.Lookups.entity.TrainingManagementStatus;
import com.nec.middleware.Lookups.entity.TrainingTypes;
import com.nec.middleware.masterdata.entity.MasterDataCity;
import com.nec.middleware.masterdata.entity.MasterDataDistrict;
import com.nec.middleware.masterdata.entity.MasterDataRegion;
import com.nec.middleware.masterdata.entity.MasterDataUniversity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "nec_training_trainee_allocations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingTraineeAllocation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "allocation_code",
            nullable = false,
            unique = true,
            length = 20
    )
    private String allocationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="region_id", nullable=false)
    private MasterDataRegion region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="district_id", nullable=false)
    private MasterDataDistrict district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="city_id", nullable=false)
    private MasterDataCity city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable=false)
    private MasterDataUniversity university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private UniversityTrainee trainee;

    // Faculty
    @Column(name = "faculty", length = 150)
    private String faculty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_class_id", nullable = false)
    private TrainingClass trainingClass;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingTypes trainingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private TrainingManagementStatus status;

    @Column(name = "allocation_date", nullable = false)
    private LocalDate allocationDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
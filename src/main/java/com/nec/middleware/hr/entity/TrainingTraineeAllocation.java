package com.nec.middleware.hr.entity;

import com.nec.middleware.Lookups.entity.TrainingManagementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Maps to: nec_training_trainee_allocations
 *
 * allocationCode → business key, auto-generated e.g. TA0001, TA0002
 *
 * FK associations:
 *  - traineeId       → nec_hr_university_trainees.id
 *  - trainingClassId → nec_training_classes.id
 *  - statusId        → nec_lkp_training_management_status.id
 *
 * Derived fields for response:
 *  - University, Region, Faculty come from UniversityTrainee
 *  - Training Type comes from TrainingClass
 *
 * Business rule:
 *  - One trainee can join only one class.
 *
 * Audit fields inherited from {@link AuditableEntity}.
 */
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

    /**
     * Business key — generated once at creation, never changed.
     * Example: TA0001
     */
    @Column(name = "allocation_code", nullable = false, unique = true, length = 20)
    private String allocationCode;

    /**
     * FK → nec_hr_university_trainees.id
     * Stored column: trainee_id
     */
    @Column(name = "trainee_id", nullable = false, insertable = false, updatable = false)
    private Long traineeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private UniversityTrainee trainee;

    /**
     * FK → nec_training_classes.id
     * Stored column: training_class_id
     */
    @Column(name = "training_class_id", nullable = false, insertable = false, updatable = false)
    private Long trainingClassId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_class_id", nullable = false)
    private TrainingClass trainingClass;

    /**
     * FK → nec_lkp_training_management_status.id
     * Stored column: status_id
     */
    @Column(name = "status_id", nullable = false, insertable = false, updatable = false)
    private Long statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private TrainingManagementStatus status;

    @Column(name = "allocation_date", nullable = false)
    private LocalDate allocationDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
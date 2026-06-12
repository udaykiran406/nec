package com.nec.middleware.hr.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Maps to: nec_training_schedules
 *
 * FK associations:
 *  - trainingClassId → nec_training_classes.id
 *
 * Audit fields inherited from {@link AuditableEntity}.
 */
@Entity
@Table(name = "nec_training_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingSchedule extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FK → nec_training_classes.id
     * Stored column: training_class_id
     */
    @Column(
            name = "training_class_id",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Long trainingClassId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_class_id", nullable = false)
    private TrainingClass trainingClass;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "time_slot", nullable = false, length = 100)
    private String timeSlot;

    @Column(name = "venue", nullable = false, length = 200)
    private String venue;

    @Column(name = "location", nullable = false, length = 200)
    private String location;
}
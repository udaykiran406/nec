package com.nec.middleware.hr.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "nec_hr_attendance_records",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_attendance_class_trainee_date",
                columnNames = {
                        "training_class_id",
                        "trainee_id",
                        "attendance_date"
                }
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingAttendanceRecord extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attendance_code", nullable = false, unique = true, length = 20)
    private String attendanceCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_class_id", nullable = false)
    private TrainingClass trainingClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id", nullable = false)
    private UniversityTrainee trainee;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "is_present", nullable = false)
    private Boolean isPresent = Boolean.TRUE;

    @Column(name = "signed_sheet_url", length = 500)
    private String signedSheetUrl;
}
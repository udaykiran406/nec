package com.nec.middleware.Lookups.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nec_lkp_job_positions", schema = "NEC")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPositions extends BaseLookupEntity {

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    private Departments department;
}

package com.nec.middleware.Lookups.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nec_lkp_performance_criteria_status")
@Setter
@Getter
@Builder
@NoArgsConstructor
public class PerformanceCriteriaStatus extends BaseLookupEntity {
}
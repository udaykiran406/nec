package com.nec.middleware.Lookups.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nec_lkp_per_diem_request_status", schema = "NEC")
@Setter
@Getter
@Builder
@NoArgsConstructor
public class PerDiemRequestStatus extends BaseLookupEntity {
}
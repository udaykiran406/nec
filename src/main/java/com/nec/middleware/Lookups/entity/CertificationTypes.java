package com.nec.middleware.Lookups.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nec_lkp_certification_types", schema = "NEC")
@Getter
@Setter
@Builder
@NoArgsConstructor
public class CertificationTypes extends BaseLookupEntity {
}

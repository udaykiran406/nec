package com.nec.middleware.Lookups.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nec_lkp_scoring_types")
@Setter
@Getter
@Builder
@NoArgsConstructor
public class ScoringTypes extends BaseLookupEntity{
}

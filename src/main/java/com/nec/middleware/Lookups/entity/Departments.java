package com.nec.middleware.Lookups.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nec_lkp_departments")
@Getter
@Setter
@Builder
@NoArgsConstructor
public class Departments extends BaseLookupEntity{
}

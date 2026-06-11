package com.nec.middleware.Lookups.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="nec_lkp_skill_levels")
@Setter
@Getter
@Builder
public class SkillLevel extends BaseLookupEntity{
}

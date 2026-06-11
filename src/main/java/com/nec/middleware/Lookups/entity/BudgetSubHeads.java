package com.nec.middleware.Lookups.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nec_lkp_budget_sub_heads")
@Getter
@Setter
@Builder
public class BudgetSubHeads extends BaseLookupEntity{
}

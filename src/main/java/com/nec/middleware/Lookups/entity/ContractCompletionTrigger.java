package com.nec.middleware.Lookups.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nec_lkp_contract_completion_trigger")
@Setter
@Getter
@Builder
@NoArgsConstructor
public class ContractCompletionTrigger  extends BaseLookupEntity{
}

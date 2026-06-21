package com.nec.middleware.Lookups.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nec_lkp_budget_sub_heads", schema = "NEC")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSubHeads extends BaseLookupEntity {

    @Column(name = "budget_head_id", nullable = false)
    private Long budgetHeadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_head_id", insertable = false, updatable = false)
    private BudgetHeads budgetHead;
}

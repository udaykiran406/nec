package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.BudgetSubHeads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetSubHeadsRepository extends JpaRepository<BudgetSubHeads, Long> {
}

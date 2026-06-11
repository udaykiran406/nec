package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.BudgetHeads;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetHeadRepository extends JpaRepository<BudgetHeads ,Long> {
}

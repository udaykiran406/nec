package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.PerformanceCriteriaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceCriteriaStatusRepository extends JpaRepository<PerformanceCriteriaStatus, Long> {
}
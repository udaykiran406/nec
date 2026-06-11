package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.JobPositions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPositions, Long> {
}

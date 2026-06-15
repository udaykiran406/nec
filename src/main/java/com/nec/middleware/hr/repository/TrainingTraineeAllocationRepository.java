package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.TrainingTraineeAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingTraineeAllocationRepository
        extends JpaRepository<TrainingTraineeAllocation, Long>,
        JpaSpecificationExecutor<TrainingTraineeAllocation> {

    Optional<TrainingTraineeAllocation> findByAllocationCode(
            String allocationCode);

    Optional<TrainingTraineeAllocation> findByAllocationCodeAndIsActiveTrue(
            String allocationCode);

    boolean existsByTraineeIdAndIsActiveTrue(
            Long traineeId);

    boolean existsByTraineeIdAndIsActiveTrueAndIdNot(
            Long traineeId,
            Long id);

    @Query("""
        SELECT COALESCE(
            MAX(CAST(SUBSTRING(a.allocationCode, 3) AS int)),
            0
        )
        FROM TrainingTraineeAllocation a
        WHERE a.allocationCode LIKE 'TA%'
    """)
    int findMaxCodeSequence();
}
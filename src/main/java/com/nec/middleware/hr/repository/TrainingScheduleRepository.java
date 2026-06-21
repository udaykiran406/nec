package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.TrainingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingScheduleRepository
        extends JpaRepository<TrainingSchedule, Long>,
        JpaSpecificationExecutor<TrainingSchedule> {

    Optional<TrainingSchedule> findByScheduleCode(String scheduleCode);

    Optional<TrainingSchedule> findByTrainingClassId(Long trainingClassId);

    @Query("""
        SELECT COALESCE(
            MAX(CAST(SUBSTRING(t.scheduleCode, 3) AS int)),
            0
        )
        FROM TrainingSchedule t
        WHERE t.scheduleCode LIKE 'TS%'
    """)
    int findMaxCodeSequence();
}
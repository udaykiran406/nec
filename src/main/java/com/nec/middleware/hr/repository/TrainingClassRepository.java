package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.TrainingClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingClassRepository
        extends JpaRepository<TrainingClass, Long>,
        JpaSpecificationExecutor<TrainingClass> {

    Optional<TrainingClass> findByClassCode(String classCode);

    boolean existsByClassNameIgnoreCase(String className);

    boolean existsByClassNameIgnoreCaseAndClassCodeNot(
            String className,
            String classCode
    );

    @Query("""
        SELECT COALESCE(
            MAX(CAST(SUBSTRING(t.classCode, 3) AS int)),
            0
        )
        FROM TrainingClass t
        WHERE t.classCode LIKE 'TC%'
    """)
    int findMaxCodeSequence();
}
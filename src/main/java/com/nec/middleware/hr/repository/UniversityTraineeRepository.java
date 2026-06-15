package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.UniversityTrainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UniversityTraineeRepository extends
        JpaRepository<UniversityTrainee, Long>,
        JpaSpecificationExecutor<UniversityTrainee> {

//    // Active record finder
//    Optional<UniversityTrainee> findByIdAndIsActiveTrue(Long id);
//
//    // Business ID finders
//    Optional<UniversityTrainee> findByUniversityTraineeIdAndIsActiveTrue(
//            String universityTraineeId
//    );

    Optional<UniversityTrainee> findByUniversityTraineeId(
            String universityTraineeId
    );

    // Duplicate checks (CREATE)
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // Duplicate checks (UPDATE)
    boolean existsByEmailAndIsActiveTrueAndIdNot(
            String email,
            Long id
    );

    boolean existsByPhoneAndIsActiveTrueAndIdNot(
            String phone,
            Long id
    );

    // Code generation helper
    @Query("""
            SELECT COALESCE(
                MAX(CAST(SUBSTRING(t.universityTraineeId, 3) AS int)), 0
            )
            FROM UniversityTrainee t
            WHERE t.universityTraineeId LIKE 'UT%'
            """)
    int findMaxCodeSequence();
}
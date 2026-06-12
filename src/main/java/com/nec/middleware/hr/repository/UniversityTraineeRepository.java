package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.UniversityTrainee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UniversityTraineeRepository extends JpaRepository<UniversityTrainee, Long> {

    Optional<UniversityTrainee> findByIdAndIsActiveTrue(Long id);

    // Duplicate checks (CREATE)
    boolean existsByEmailAndIsActiveTrue(String email);
    boolean existsByPhoneAndIsActiveTrue(String phone);

    // Duplicate checks (UPDATE — exclude self)
    boolean existsByEmailAndIsActiveTrueAndIdNot(String email, Long id);
    boolean existsByPhoneAndIsActiveTrueAndIdNot(String phone, Long id);

    // Code generation helper
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(t.code, 3) AS int)), 0) FROM UniversityTrainee t WHERE t.code LIKE 'UT%'")
    int findMaxCodeSequence();

    @Query("""
            SELECT t FROM UniversityTrainee t
            WHERE (:universityId IS NULL OR t.universityId = :universityId)
              AND (:regionId     IS NULL OR t.regionId     = :regionId)
              AND (:districtId   IS NULL OR t.districtId   = :districtId)
              AND (:cityId       IS NULL OR t.cityId       = :cityId)
              AND (:statusId     IS NULL OR t.statusId     = :statusId)
              AND (:isActive     IS NULL OR t.isActive     = :isActive)
            """)
    Page<UniversityTrainee> findAllWithFilters(
            @Param("universityId") Long universityId,
            @Param("regionId")     Long regionId,
            @Param("districtId")   Long districtId,
            @Param("cityId")       Long cityId,
            @Param("statusId")     Long statusId,
            @Param("isActive")     Boolean isActive,
            Pageable pageable
    );
}

package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.Aaqil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AaqilRepository extends JpaRepository<Aaqil, Long> {

    Optional<Aaqil> findByIdAndIsActiveTrue(Long id);

    // Duplicate checks (CREATE)
    boolean existsByEmailAndIsActiveTrue(String email);
    boolean existsByPhoneAndIsActiveTrue(String phone);

    // Duplicate checks (UPDATE — exclude self)
    boolean existsByEmailAndIsActiveTrueAndIdNot(String email, Long id);
    boolean existsByPhoneAndIsActiveTrueAndIdNot(String phone, Long id);

    // Code generation helper
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(a.code, 3) AS int)), 0) FROM Aaqil a WHERE a.code LIKE 'AA%'")
    int findMaxCodeSequence();

    @Query("""
            SELECT a FROM Aaqil a
            WHERE (:aaqilTypeId IS NULL OR a.aaqilTypeId = :aaqilTypeId)
              AND (:regionId    IS NULL OR a.regionId    = :regionId)
              AND (:districtId  IS NULL OR a.districtId  = :districtId)
              AND (:cityId      IS NULL OR a.cityId      = :cityId)
              AND (:statusId    IS NULL OR a.statusId    = :statusId)
              AND (:isActive    IS NULL OR a.isActive    = :isActive)
            """)
    Page<Aaqil> findAllWithFilters(
            @Param("aaqilTypeId") Long aaqilTypeId,
            @Param("regionId")    Long regionId,
            @Param("districtId")  Long districtId,
            @Param("cityId")      Long cityId,
            @Param("statusId")    Long statusId,
            @Param("isActive")    Boolean isActive,
            Pageable pageable
    );
}

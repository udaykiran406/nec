package com.nec.middleware.hr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortalUserRepository extends JpaRepository<com.nec.middleware.portal.entity.PortalUser, Long> {

    /**
     * Find active (non-deleted) record by id
     */
    Optional<com.nec.middleware.portal.entity.PortalUser> findByIdAndIsDeletedFalse(Long id);

    // ------------------------------------------------------------------ Duplicate checks (CREATE)

    boolean existsByPhoneAndIsDeletedFalse(String phone);

    boolean existsByEmailAndIsDeletedFalse(String email);

    // ------------------------------------------------------------------ Duplicate checks (UPDATE — exclude self)

    boolean existsByPhoneAndIsDeletedFalseAndIdNot(String phone, Long id);

    boolean existsByEmailAndIsDeletedFalseAndIdNot(String email, Long id);
// ------------------------------------------------------------------ Code generation helper

    /**
     * Returns the highest numeric suffix currently stored in any PU-prefixed code,
     * or 0 if no records exist yet.
     * Example stored codes: PU001, PU002 → returns 2.
     */
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(u.code, 3) AS int)), 0) FROM PortalUser u WHERE u.code LIKE 'PU%'")
    int findMaxCodeSequence();

    // ------------------------------------------------------------------ Filtered paginated list

    // ------------------------------------------------------------------ Filtered paginated list

    /**
     * Returns non-deleted portal users matching all provided optional filters.
     * Null parameters are treated as "no filter".
     */
    @Query("""
            SELECT u FROM PortalUser u
            WHERE u.isDeleted = false
              AND (:roleId           IS NULL OR u.roleId           = :roleId)
              AND (:genderId         IS NULL OR u.genderId         = :genderId)
              AND (:departmentId     IS NULL OR u.departmentId     = :departmentId)
              AND (:regionId         IS NULL OR u.regionId         = :regionId)
              AND (:districtId       IS NULL OR u.districtId       = :districtId)
              AND (:cityId           IS NULL OR u.cityId           = :cityId)
              AND (:portalUserTypeId IS NULL OR u.portalUserTypeId = :portalUserTypeId)
              AND (:referenceId      IS NULL OR u.referenceId      = :referenceId)
              AND (:isActive         IS NULL OR u.isActive         = :isActive)
            """)
    Page<com.nec.middleware.portal.entity.PortalUser> findAllWithFilters(
            @Param("roleId")           Long roleId,
            @Param("genderId")         Long genderId,
            @Param("departmentId")     Long departmentId,
            @Param("regionId")         Long regionId,
            @Param("districtId")       Long districtId,
            @Param("cityId")           Long cityId,
            @Param("portalUserTypeId") Long portalUserTypeId,
            @Param("referenceId")      Long referenceId,
            @Param("isActive")         Boolean isActive,
            Pageable pageable
    );
}

package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.PortalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {

    /**
     * Find active (non-deleted) record by id
     */
    Optional<PortalUser> findByPortalUserIdAndIsActiveTrue(String portalUserId);

    Optional<PortalUser> findByPortalUserIdAndIsDeletedFalse(String portalUserId);

    // ------------------------------------------------------------------ Duplicate checks (CREATE)

    boolean existsByPhoneAndIsActiveTrue(String phone);

    boolean existsByEmailAndIsActiveTrue(String email);

// ------------------------------------------------------------------ Code generation helper

    /**
     * Returns the highest numeric suffix currently stored in any PU-prefixed code,
     * or 0 if no records exist yet.
     * Example stored codes: PU001, PU002 → returns 2.
     */
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(u.portalUserId, 3) AS int)), 0) FROM PortalUser u WHERE u.portalUserId LIKE 'PU%'")
    int findMaxCodeSequence();

    // ------------------------------------------------------------------ Filtered paginated list

    // ------------------------------------------------------------------ Filtered paginated list

    /**
     * Returns non-deleted portal users matching all provided optional filters.
     * Null parameters are treated as "no filter".
     */
    @Query("""
    SELECT u
    FROM PortalUser u
    WHERE u.isDeleted = false
       AND (:userName IS NULL OR u.userName LIKE %:userName%)
      AND (:portalUserId IS NULL OR u.portalUserId LIKE %:portalUserId%)
      AND (:roleId IS NULL OR u.roleId = :roleId)
      AND (:genderId IS NULL OR u.genderId = :genderId)
      AND (:universityId IS NULL OR u.universityId = :universityId)
      AND (:regionId IS NULL OR u.regionId = :regionId)
      AND (:districtId IS NULL OR u.districtId = :districtId)
      AND (:cityId IS NULL OR u.cityId = :cityId)
      AND (:portalUserTypeId IS NULL OR u.portalUserTypeId = :portalUserTypeId)
      AND (:isActive IS NULL OR u.isActive = :isActive)
""")
    Page<PortalUser> findAllWithFilters(
            @Param("userName") String userName,
            @Param("portalUserId") String portalUserId,
            @Param("roleId") Long roleId,
            @Param("genderId") Long genderId,
            @Param("universityId") Long universityId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("cityId") Long cityId,
            @Param("portalUserTypeId") Long portalUserTypeId,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );


}

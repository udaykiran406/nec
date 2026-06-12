package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.PortalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortalUserRepository extends JpaRepository<PortalUser, Long> , JpaSpecificationExecutor<PortalUser> {

    /**
     * Find active (non-deleted) record by id
     */

    Optional<PortalUser> findByPortalUserIdAndIsActiveTrue(String portalUserId);

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
//    @Query("""
//            SELECT p
//            FROM PortalUser p
//            WHERE (:userName IS NULL OR LOWER(p.userName) LIKE LOWER(CONCAT('%', :userName, '%')))
//            AND (:portalUserId IS NULL OR p.portalUserId LIKE CONCAT('%', :portalUserId, '%'))
//            AND (:genderId IS NULL OR p.gender.id = :genderId)
//            AND (:roleId IS NULL OR p.role.id = :roleId)
//            AND (:universityId IS NULL OR p.university.id = :universityId)
//            AND (:regionId IS NULL OR p.region.id = :regionId)
//            AND (:districtId IS NULL OR p.district.id = :districtId)
//            AND (:cityId IS NULL OR p.city.id = :cityId)
//            AND (:portalUserTypeId IS NULL OR p.portalUserType.id = :portalUserTypeId)
//            AND (:isActive IS NULL OR p.isActive = :isActive)
//            """)
//    Page<PortalUser> findAllPortalUsersWithFilters(
//            @Param("userName") String userName,
//            @Param("portalUserId") String portalUserId,
//            @Param("genderId") Long genderId,
//            @Param("roleId") Long roleId,
//            @Param("universityId") Long universityId,
//            @Param("regionId") Long regionId,
//            @Param("districtId") Long districtId,
//            @Param("cityId") Long cityId,
//            @Param("portalUserTypeId") Long portalUserTypeId,
//            @Param("isActive") Boolean isActive,
//            Pageable pageable);


}

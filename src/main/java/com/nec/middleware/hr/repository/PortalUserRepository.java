package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface PortalUserRepository extends JpaRepository<PortalUser, Long> , JpaSpecificationExecutor<PortalUser> {

    /**
     * Find active (non-deleted) record by id
     */

    Optional<PortalUser> findByPortalUserIdAndIsActiveTrue(String portalUserId);
    Optional<PortalUser> findByPortalUserId(String portalUserId);
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







}

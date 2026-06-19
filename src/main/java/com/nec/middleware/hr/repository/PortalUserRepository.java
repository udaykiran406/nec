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


    Optional<PortalUser> findByPortalUserId(String portalUserId);
    // ------------------------------------------------------------------ Duplicate checks (CREATE)

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    //--------------------------------------------------------------For Update
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);

}

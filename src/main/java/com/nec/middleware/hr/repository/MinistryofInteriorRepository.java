package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.MinistryofInterior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MinistryofInteriorRepository extends JpaRepository<MinistryofInterior, Long>,
        JpaSpecificationExecutor<MinistryofInterior> {



    Optional<MinistryofInterior> findByMinistryofInteriorId(String ministryofInteriorId);

    // Duplicate checks (CREATE)
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // Duplicate checks (UPDATE)
    boolean existsByEmailAndIdNot(
            String email,
            Long id
    );

    boolean existsByPhoneAndIdNot(
            String phone,
            Long id
    );

}
package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.PoliticalPartyAgent;
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
public interface PoliticalPartyAgentRepository extends JpaRepository<PoliticalPartyAgent, Long>  , JpaSpecificationExecutor<PoliticalPartyAgent> {

    Optional<PoliticalPartyAgent> findByIdAndIsActiveTrue(Long id);

    // Duplicate checks (CREATE)
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    // For Update
    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);


    Optional<PoliticalPartyAgent> findByPoliticalPartyAgentUserId(String agentUserId);

}

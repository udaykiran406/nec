package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.ThirdPartyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThirdPartyStatusRepository extends JpaRepository<ThirdPartyStatus, Long> {

    Optional<ThirdPartyStatus> findByCode(String code);
}

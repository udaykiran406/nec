package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.ThirdPartyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdPartyStatusRepository extends JpaRepository<ThirdPartyStatus, Long> {
}

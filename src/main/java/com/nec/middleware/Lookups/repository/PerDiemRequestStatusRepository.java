package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.PerDiemRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerDiemRequestStatusRepository extends JpaRepository<PerDiemRequestStatus, Long> {
}
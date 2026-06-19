package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.DeploymentReportingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentReportingStatusRepository extends JpaRepository<DeploymentReportingStatus, Long> {
}
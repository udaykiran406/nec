package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.DeploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentStatusRepository extends JpaRepository<DeploymentStatus,Long> {
}

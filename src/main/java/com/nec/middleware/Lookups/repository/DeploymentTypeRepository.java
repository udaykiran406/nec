package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.DeploymentTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentTypeRepository extends JpaRepository<DeploymentTypes, Long> {
}

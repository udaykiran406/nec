package com.nec.middleware.workflow.repository;

import com.nec.middleware.workflow.entity.WorkflowSlaConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowSlaConfigurationRepository extends JpaRepository<WorkflowSlaConfiguration, Long> {

    Optional<WorkflowSlaConfiguration> findByModuleName(String moduleName);
}
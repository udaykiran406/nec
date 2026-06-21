package com.nec.middleware.workflow.repository;

import com.nec.middleware.workflow.entity.WorkflowEscalationMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowEscalationMatrixRepository
        extends JpaRepository<WorkflowEscalationMatrix, Long> {

    Optional<WorkflowEscalationMatrix> findByModuleNameAndApprovalRole(String moduleName, String approvalRole);
}
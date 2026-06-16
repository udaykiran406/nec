package com.nec.middleware.workflow.repository;

import com.nec.middleware.workflow.entity.WorkflowAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowAuditRepository extends JpaRepository<WorkflowAudit, Long> {

    List<WorkflowAudit> findByModuleNameAndEntityIdOrderByApprovalLevel(String moduleName,String entityId);
}


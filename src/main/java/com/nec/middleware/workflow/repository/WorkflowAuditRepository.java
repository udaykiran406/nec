package com.nec.middleware.workflow.repository;

import com.nec.middleware.workflow.dto.response.WorkflowInboxDto;
import com.nec.middleware.workflow.entity.WorkflowAudit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowAuditRepository extends JpaRepository<WorkflowAudit, Long>, JpaSpecificationExecutor<WorkflowAudit> {

    List<WorkflowAudit> findByModuleNameAndEntityIdOrderByApprovalLevel(String moduleName,String entityId);

    Page<WorkflowAudit> findByApprovalRoleAndAction(String approvalRole,String action,Pageable pageable);
    List<WorkflowAudit> findByModuleNameAndEntityId(String moduleName, String entityId);
}


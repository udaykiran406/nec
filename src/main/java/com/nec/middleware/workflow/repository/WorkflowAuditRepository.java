package com.nec.middleware.workflow.repository;

import com.nec.middleware.workflow.entity.WorkflowAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowAuditRepository extends JpaRepository<WorkflowAudit, Long>, JpaSpecificationExecutor<WorkflowAudit> {

    List<WorkflowAudit> findByModuleNameAndEntityIdOrderByApprovalLevel(String moduleName, String entityId);

    Page<WorkflowAudit> findByApprovalRoleAndAction(String approvalRole, String action, Pageable pageable);

    List<WorkflowAudit> findByModuleNameAndEntityId(String moduleName, String entityId);

    List<WorkflowAudit> findByModuleNameAndEntityIdAndRevisionNoOrderByApprovalLevel(String moduleName, String entityId, Integer revisionNo);

    @Query("""
            select coalesce(max(w.revisionNo),1)
            from WorkflowAudit w
            where w.moduleName=:moduleName
            and w.entityId=:entityId
            """)
    Integer findMaxRevisionNo(String moduleName, String entityId);

    @Query("""
            select w
            from WorkflowAudit w
            where w.moduleName = :moduleName
            and w.entityId = :entityId
            and w.revisionNo = (
                select max(w2.revisionNo)
                from WorkflowAudit w2
                where w2.moduleName = :moduleName
                and w2.entityId = :entityId
            )
            order by w.approvalLevel
            """)
    List<WorkflowAudit> findLatestRevisionAudits(String moduleName, String entityId);

    @Query("""
            SELECT w FROM WorkflowAudit w
            WHERE w.requesterRole = :role
            AND w.action = 'REJECTED'
            AND w.revisionNo = (
                SELECT MAX(w2.revisionNo)
                FROM WorkflowAudit w2
                WHERE w2.moduleName = w.moduleName
                AND w2.entityId = w.entityId
            )
            ORDER BY w.actionDate DESC
            """)
    Page<WorkflowAudit> findRejectedForRequesterRole(@Param("role") String role, Pageable pageable);
}


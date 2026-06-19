package com.nec.middleware.masterdata.repository;

import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalWorkflowLevelRepository
        extends JpaRepository<ApprovalWorkflowLevel, Long> {
    List<ApprovalWorkflowLevel> findByModuleNameOrderByLevelOrder(String moduleName);

    Optional<ApprovalWorkflowLevel> findByModuleNameAndLevelOrder(String temporaryContract, int i);
}

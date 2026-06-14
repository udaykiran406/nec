package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.ApprovalLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApprovalLevelRepository
        extends JpaRepository<ApprovalLevel, Long> {
    Optional<ApprovalLevel> findByModuleNameAndLevelOrder(String moduleName, Integer levelOrder);
}

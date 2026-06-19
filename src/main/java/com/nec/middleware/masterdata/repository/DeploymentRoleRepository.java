package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataDeploymentRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DeploymentRoleRepository
        extends JpaRepository<MasterDataDeploymentRole, Long> {

    Optional<MasterDataDeploymentRole> findByIdAndIsDeleted(
            Long id,
            Short isDeleted
    );

    List<MasterDataDeploymentRole>
    findAllByIsDeletedOrderByRoleNameAsc(
            Short isDeleted
    );

    boolean existsByRoleNameIgnoreCaseAndIsDeleted(
            String roleName,
            Short isDeleted
    );

    boolean existsByRoleNameIgnoreCaseAndIsDeletedAndIdNot(
            String roleName,
            Short isDeleted,
            Long id
    );

    Page<MasterDataDeploymentRole> findAllByIsDeletedOrderByRoleNameAsc(Short isDeleted, Pageable pageable);
}
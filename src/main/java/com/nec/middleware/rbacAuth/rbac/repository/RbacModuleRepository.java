package com.nec.middleware.rbacAuth.rbac.repository;

import com.nec.middleware.rbacAuth.rbac.entity.RbacModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link RbacModule} (nec_rbac_module).
 *
 * <p>All finder methods automatically exclude soft-deleted records
 * (i.e. {@code is_deleted = false}).
 */
@Repository
public interface RbacModuleRepository extends JpaRepository<RbacModule, Long>,
        JpaSpecificationExecutor<RbacModule> {

    /**
     * Finds a non-deleted module by its primary key.
     *
     * @param moduleId the primary key
     * @param isDeleted soft-delete flag (pass {@code false} for active records)
     * @return an Optional containing the module if found
     */
    Optional<RbacModule> findByModuleIdAndIsDeleted(Long moduleId, Integer isDeleted);

    /**
     * Returns all non-deleted modules ordered by display order.
     *
     * @param isDeleted soft-delete flag (pass {@code false} for active records)
     * @return list of active modules
     */
    List<RbacModule> findAllByIsDeletedOrderByDisplayOrderAsc(Integer isDeleted);

    /**
     * Checks whether a module with the given code already exists (case-insensitive).
     *
     * @param moduleCode the code to check
     * @param isDeleted soft-delete flag (pass {@code false})
     * @return {@code true} if the code already exists in an active record
     */
    boolean existsByModuleCodeIgnoreCaseAndIsDeleted(String moduleCode, Integer isDeleted);

    /**
     * Checks whether a module code already exists (case-insensitive),
     * excluding a specific ID (used for update duplicate checks).
     *
     * @param moduleCode the code to check
     * @param moduleId the ID to exclude
     * @return {@code true} if a duplicate exists
     */
    @Query("SELECT COUNT(m) > 0 FROM RbacModule m " +
           "WHERE LOWER(m.moduleCode) = LOWER(:moduleCode) " +
           "AND m.moduleId <> :moduleId AND m.isDeleted = 0")
    boolean existsByModuleCodeIgnoreCaseAndModuleIdNot(
            @Param("moduleCode") String moduleCode,
            @Param("moduleId") Long moduleId);

    /**
     * Finds all non-deleted modules matching a specific status.
     *
     * @param status the status to filter by ('ACTIVE' or 'INACTIVE')
     * @param isDeleted soft-delete flag (pass {@code false})
     * @return list of matching modules
     */
    List<RbacModule> findAllByStatusAndIsDeletedOrderByDisplayOrderAsc(
            String status, Integer isDeleted);
}



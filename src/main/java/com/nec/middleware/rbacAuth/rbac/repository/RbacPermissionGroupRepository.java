package com.nec.middleware.rbacAuth.rbac.repository;

import com.nec.middleware.rbacAuth.rbac.entity.RbacPermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link RbacPermissionGroup}.
 */
@Repository
public interface RbacPermissionGroupRepository extends JpaRepository<RbacPermissionGroup, Long>,
        JpaSpecificationExecutor<RbacPermissionGroup> {

    /**
     * Finds a permission group by its ID and module ID.
     *
     * @param groupId the group ID
     * @param moduleId the module ID
     * @return Optional containing the group if found
     */
    Optional<RbacPermissionGroup> findByGroupIdAndModuleId(Long groupId, Long moduleId);

    /**
     * Finds a permission group by its ID.
     *
     * @param groupId the group ID
     * @return Optional containing the group if found
     */
    Optional<RbacPermissionGroup> findByGroupId(Long groupId);

    /**
     * Finds a permission group by ID with module association fetched.
     *
     * @param groupId the group ID
     * @return Optional containing the group with module if found
     */
    @Query("SELECT g FROM RbacPermissionGroup g JOIN FETCH g.module WHERE g.groupId = :groupId")
    Optional<RbacPermissionGroup> findByGroupIdWithAssociations(@Param("groupId") Long groupId);

    /**
     * Finds all permission groups by module ID and status, ordered by display order.
     *
     * @param moduleId the module ID
     * @param status the status filter
     * @return list of permission groups ordered by display order
     */
    List<RbacPermissionGroup> findByGroupIdAndModuleIdAndStatusOrderByDisplayOrderAsc(Long groupId, Long moduleId, String status);

    /**
     * Finds all permission groups by module ID ordered by display order ascending.
     *
     * @param moduleId the module ID
     * @return list of permission groups ordered by display order
     */
    List<RbacPermissionGroup> findByModuleIdOrderByDisplayOrderAsc(Long moduleId);

    /**
     * Finds all active (non-deleted) permission groups by module ID and status, ordered by display order.
     * Filters out deleted groups.
     *
     * @param moduleId the module ID
     * @param status the status filter (e.g., 'ACTIVE')
     * @return list of permission groups with matching status ordered by display order
     */
    List<RbacPermissionGroup> findByModuleIdAndStatusAndIsDeletedOrderByDisplayOrderAsc(
            Long moduleId, String status, Integer isDeleted);

    /**
     * Checks if a group code exists within a module.
     *
     * @param groupCode the group code
     * @param moduleId the module ID
     * @return true if exists, false otherwise
     */
    boolean existsByGroupCodeAndModuleId(String groupCode, Long moduleId);
}





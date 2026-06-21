package com.nec.middleware.rbacAuth.rbac.repository;

import com.nec.middleware.rbacAuth.rbac.entity.RbacPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link RbacPermission}.
 */
@Repository
public interface RbacPermissionRepository extends JpaRepository<RbacPermission, Long>,
        JpaSpecificationExecutor<RbacPermission> {

    /**
     * Finds a permission by ID, module ID, and group ID.
     *
     * @param permissionId the permission ID
     * @param moduleId the module ID
     * @param groupId the group ID
     * @return Optional containing the permission if found
     */
    Optional<RbacPermission> findByPermissionIdAndModuleIdAndGroupId(Long permissionId, Long moduleId, Long groupId);

    /**
     * Finds a permission by ID, group ID, module ID, and status.
     *
     * @param permissionId the permission ID
     * @param groupId the group ID
     * @param moduleId the module ID
     * @param status the status filter
     * @return Optional containing the permission if found
     */
    Optional<RbacPermission> findByPermissionIdAndGroupIdAndModuleIdAndStatus(
            Long permissionId, Long groupId, Long moduleId, String status);

    /**
     * Finds a permission by ID.
     *
     * @param permissionId the permission ID
     * @return Optional containing the permission if found
     */
    Optional<RbacPermission> findByPermissionId(Long permissionId);

    /**
     * Finds a permission by ID with module and group associations fetched.
     *
     * @param permissionId the permission ID
     * @return Optional containing the permission with associations if found
     */
    @Query("SELECT p FROM RbacPermission p "
            + "JOIN FETCH p.module "
            + "JOIN FETCH p.group "
            + "WHERE p.permissionId = :permissionId")
    Optional<RbacPermission> findByPermissionIdWithAssociations(@Param("permissionId") Long permissionId);

    /**
     * Finds all permissions by group ID ordered by display order ascending.
     *
     * @param groupId the group ID
     * @return list of permissions ordered by display order
     */
    List<RbacPermission> findByGroupIdOrderByDisplayOrderAsc(Long groupId);

    /**
     * Finds all active (non-deleted) permissions by group ID and status, ordered by display order.
     * Filters out deleted permissions.
     *
     * @param groupId the group ID
     * @param status the status filter (e.g., 'ACTIVE')
     * @return list of permissions with matching status ordered by display order
     */
    List<RbacPermission> findByGroupIdAndStatusAndIsDeletedOrderByDisplayOrderAsc(
            Long groupId, String status, Integer isDeleted);

    /**
     * Finds all permissions by module ID ordered by display order ascending.
     *
     * @param moduleId the module ID
     * @return list of permissions ordered by display order
     */
    List<RbacPermission> findByModuleIdOrderByDisplayOrderAsc(Long moduleId);

    /**
     * Checks if a permission code exists within a module.
     *
     * @param permissionCode the permission code
     * @param moduleId the module ID
     * @return true if exists, false otherwise
     */
    boolean existsByPermissionCodeAndModuleId(String permissionCode, Long moduleId);
}





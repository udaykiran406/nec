package com.nec.middleware.rbacAuth.rbac.repository;

import com.nec.middleware.rbacAuth.rbac.entity.RbacRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link RbacRolePermission} (rbac_role_permission).
 *
 * <p>Manages role-permission mappings with module and group references.
 */
@Repository
public interface RbacRolePermissionRepository extends JpaRepository<RbacRolePermission, Long>,
        JpaSpecificationExecutor<RbacRolePermission> {

    /**
     * Finds a role-permission mapping by its primary key.
     *
     * @param rolePermissionId the primary key
     * @return an Optional containing the mapping if found
     */
    Optional<RbacRolePermission> findByRolePermissionId(Long rolePermissionId);

    /**
     * Finds a role-permission mapping by primary key with all associations fetched.
     *
     * @param rolePermissionId the primary key
     * @return an Optional containing the mapping with associations if found
     */
    @Query("SELECT rp FROM RbacRolePermission rp "
            + "JOIN FETCH rp.role "
            + "JOIN FETCH rp.module "
            + "JOIN FETCH rp.group "
            + "JOIN FETCH rp.permission "
            + "WHERE rp.rolePermissionId = :rolePermissionId")
    Optional<RbacRolePermission> findByRolePermissionIdWithAssociations(
            @Param("rolePermissionId") Long rolePermissionId);

    /**
     * Checks if a specific role-permission mapping already exists.
     *
     * @param roleId       the role ID
     * @param moduleId     the module ID
     * @param groupId      the group ID
     * @param permissionId the permission ID
     * @return true if the mapping exists
     */
    boolean existsByRoleIdAndModuleIdAndGroupIdAndPermissionId(
            Long roleId, Long moduleId, Long groupId, Long permissionId);

    /**
     * Returns {@code true} when the role has at least one active mapping for any of the given permission codes.
     */
    @Query("SELECT COUNT(rp) > 0 FROM RbacRolePermission rp "
            + "JOIN rp.permission p "
            + "WHERE rp.roleId = :roleId AND rp.status = :status "
            + "AND p.permissionCode IN :permissionCodes")
    boolean existsByRoleIdAndStatusAndPermissionCodeIn(
            @Param("roleId") Long roleId,
            @Param("status") String status,
            @Param("permissionCodes") Collection<String> permissionCodes);

    /**
     * Finds all active role-permission mappings for a specific role.
     *
     * @param roleId the role ID
     * @param status the status filter (e.g., 'ACTIVE')
     * @return list of role-permission mappings for the role
     */
    List<RbacRolePermission> findAllByRoleIdAndStatus(Long roleId, String status);

    /**
     * Finds all role-permission mappings for a specific role and module.
     *
     * @param roleId   the role ID
     * @param moduleId the module ID
     * @return list of mappings
     */
    List<RbacRolePermission> findAllByRoleIdAndModuleId(Long roleId, Long moduleId);

    /**
     * Finds all role-permission mappings for a specific role, module, and group.
     *
     * @param roleId   the role ID
     * @param moduleId the module ID
     * @param groupId  the group ID
     * @return list of mappings
     */
    List<RbacRolePermission> findAllByRoleIdAndModuleIdAndGroupId(Long roleId, Long moduleId, Long groupId);

    /**
     * Deletes all role-permission mappings for a specific role and permission.
     *
     * @param roleId       the role ID
     * @param permissionId the permission ID
     */
    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);

    /**
     * Counts the number of role-permission mappings for a specific role.
     *
     * @param roleId the role ID
     * @return count of mappings
     */
    long countByRoleId(Long roleId);

    /**
     * Finds all role-permission mappings for a specific role, module, group, and permission ID.
     * Used for soft-delete operations.
     *
     * @param roleId       the role ID
     * @param moduleId     the module ID
     * @param groupId      the group ID
     * @param permissionId the permission ID
     * @return list of mappings
     */
    List<RbacRolePermission> findByRoleIdAndModuleIdAndGroupIdAndPermissionId(
            Long roleId, Long moduleId, Long groupId, Long permissionId);

    /**
     * Finds all role-permission mappings with a specific status.
     *
     * @param status the status filter (e.g., 'ACTIVE')
     * @return list of role-permission mappings with the given status
     */
    List<RbacRolePermission> findAllByStatus(String status);

    /**
     * Finds a specific role-permission mapping for a role, module, group, and permission.
     * Used for checking existence and update operations.
     *
     * @param roleId       the role ID
     * @param moduleId     the module ID
     * @param groupId      the group ID
     * @param permissionId the permission ID
     * @return Optional containing the mapping if found (returns first result)
     */
    @Query("SELECT rp FROM RbacRolePermission rp WHERE rp.roleId = :roleId AND rp.moduleId = :moduleId " +
            "AND rp.groupId = :groupId AND rp.permissionId = :permissionId")
    Optional<RbacRolePermission> findOptionalByRoleIdAndModuleIdAndGroupIdAndPermissionId(
            @Param("roleId") Long roleId,
            @Param("moduleId") Long moduleId,
            @Param("groupId") Long groupId,
            @Param("permissionId") Long permissionId);

     /**
      * Retrieves all active role-permission mappings for a specific role with related entities loaded.
      *
      * @param roleId the role ID
      * @param status the status filter (e.g., 'ACTIVE')
      * @return list of role-permission mappings for the role
      */
     @Query("SELECT rp FROM RbacRolePermission rp "
             + "JOIN FETCH rp.role "
             + "JOIN FETCH rp.module "
             + "JOIN FETCH rp.group "
             + "JOIN FETCH rp.permission "
             + "WHERE rp.roleId = :roleId AND rp.status = :status")
     List<RbacRolePermission> findAllByRoleIdAndStatusWithEagerLoading(
             @Param("roleId") Long roleId,
             @Param("status") String status);

     /**
      * Retrieves all active role-permission mappings with related entities loaded.
      *
      * @param status the status filter (e.g., 'ACTIVE')
      * @return list of all role-permission mappings with the given status
      */
     @Query("SELECT rp FROM RbacRolePermission rp "
             + "JOIN FETCH rp.role "
             + "JOIN FETCH rp.module "
             + "JOIN FETCH rp.group "
             + "JOIN FETCH rp.permission "
             + "WHERE rp.status = :status")
     List<RbacRolePermission> findAllByStatusWithEagerLoading(@Param("status") String status);

     /**
      * Retrieves role-permission mappings for a role and module with related entities loaded.
      *
      * @param roleId   the role ID
      * @param moduleId the module ID
      * @return list of mappings with associations fetched
      */
     @Query("SELECT rp FROM RbacRolePermission rp "
             + "JOIN FETCH rp.role "
             + "JOIN FETCH rp.module "
             + "JOIN FETCH rp.group "
             + "JOIN FETCH rp.permission "
             + "WHERE rp.roleId = :roleId AND rp.moduleId = :moduleId")
     List<RbacRolePermission> findAllByRoleIdAndModuleIdWithAssociations(
             @Param("roleId") Long roleId,
             @Param("moduleId") Long moduleId);
}


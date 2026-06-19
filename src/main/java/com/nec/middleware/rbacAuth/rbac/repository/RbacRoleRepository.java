package com.nec.middleware.rbacAuth.rbac.repository;

import com.nec.middleware.rbacAuth.rbac.entity.RbacRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link RbacRole} (nec_rbac_roles).
 *
 * <p>All finder methods automatically exclude soft-deleted records
 * (i.e. {@code is_deleted = false}).
 */
@Repository
public interface RbacRoleRepository extends JpaRepository<RbacRole, Long>,
        JpaSpecificationExecutor<RbacRole> {

    /**
     * Finds a non-deleted role by its primary key.
     *
     * @param roleId    the primary key
     * @param isDeleted soft-delete flag (pass {@code false} for active records)
     * @return an Optional containing the role if found
     */
    Optional<RbacRole> findByRoleIdAndIsDeleted(Long roleId, Integer isDeleted);

    /**
     * Finds a non-deleted role by primary key with parent role association fetched.
     *
     * @param roleId    the primary key
     * @param isDeleted soft-delete flag (pass {@code false} for active records)
     * @return an Optional containing the role with associations if found
     */
    @Query("SELECT r FROM RbacRole r LEFT JOIN FETCH r.parentRole "
            + "WHERE r.roleId = :roleId AND r.isDeleted = :isDeleted")
    Optional<RbacRole> findByRoleIdAndIsDeletedWithAssociations(
            @Param("roleId") Long roleId,
            @Param("isDeleted") Integer isDeleted);

    /**
     * Returns all non-deleted roles ordered alphabetically by role name.
     *
     * @param isDeleted soft-delete flag (pass {@code false} for active records)
     * @return list of active roles
     */
    List<RbacRole> findAllByIsDeletedOrderByRoleNameAsc(Integer isDeleted);

    @Query("SELECT DISTINCT r FROM RbacRole r LEFT JOIN FETCH r.parentRole "
            + "WHERE r.isDeleted = :isDeleted ORDER BY r.roleName")
    List<RbacRole> findAllByIsDeletedWithAssociations(@Param("isDeleted") Integer isDeleted);

    /**
     * Checks whether a role with the given code already exists (case-insensitive),
     * excluding a specific ID (used for update duplicate checks).
     *
     * @param roleCode the code to check
     * @param roleId   the ID to exclude
     * @return {@code true} if a duplicate exists
     */
    @Query("SELECT COUNT(r) > 0 FROM RbacRole r " +
            "WHERE LOWER(r.roleCode) = LOWER(:roleCode) " +
            "AND r.roleId <> :roleId AND r.isDeleted = 0")
    boolean existsByRoleCodeIgnoreCaseAndRoleIdNot(
            @Param("roleCode") String roleCode,
            @Param("roleId") Long roleId);

    /**
     * Checks whether a role code already exists (used during creation).
     *
     * @param roleCode  the code to check
     * @param isDeleted soft-delete flag (pass {@code false})
     * @return {@code true} if the code already exists in an active record
     */
    boolean existsByRoleCodeIgnoreCaseAndIsDeleted(String roleCode, Integer isDeleted);

    /**
     * Checks whether a role with the given name already exists (case-insensitive),
     * excluding a specific ID (used for update duplicate checks).
     *
     * @param roleName the name to check
     * @param roleId   the ID to exclude
     * @return {@code true} if a duplicate exists
     */
    @Query("SELECT COUNT(r) > 0 FROM RbacRole r " +
            "WHERE LOWER(r.roleName) = LOWER(:roleName) " +
            "AND r.roleId <> :roleId AND r.isDeleted = 0")
    boolean existsByRoleNameIgnoreCaseAndRoleIdNot(
            @Param("roleName") String roleName,
            @Param("roleId") Long roleId);

    /**
     * Checks whether a role name already exists (used during creation).
     *
     * @param roleName  the name to check
     * @param isDeleted soft-delete flag (pass {@code false})
     * @return {@code true} if the name already exists in an active record
     */
    boolean existsByRoleNameIgnoreCaseAndIsDeleted(String roleName, Integer isDeleted);

    /**
     * Finds all non-deleted roles matching a specific status.
     *
     * @param status    the status to filter by ('ACTIVE' or 'INACTIVE')
     * @param isDeleted soft-delete flag (pass {@code false})
     * @return list of matching roles
     */
    List<RbacRole> findAllByStatusAndIsDeletedOrderByRoleNameAsc(
            String status, Integer isDeleted);

    @Query("SELECT DISTINCT r FROM RbacRole r LEFT JOIN FETCH r.parentRole "
            + "WHERE r.status = :status AND r.isDeleted = :isDeleted ORDER BY r.roleName")
    List<RbacRole> findAllByStatusAndIsDeletedWithAssociations(
            @Param("status") String status,
            @Param("isDeleted") Integer isDeleted);

    /**
     * Finds all child roles of a given parent role.
     *
     * @param parentRoleId the parent role ID
     * @param isDeleted    soft-delete flag (pass {@code false})
     * @return list of child roles
     */
    List<RbacRole> findAllByParentRoleIdAndIsDeletedOrderByRoleNameAsc(
            Long parentRoleId, Integer isDeleted);

    @Query("SELECT DISTINCT r FROM RbacRole r LEFT JOIN FETCH r.parentRole "
            + "WHERE r.parentRoleId = :parentRoleId AND r.isDeleted = :isDeleted ORDER BY r.roleName")
    List<RbacRole> findAllByParentRoleIdAndIsDeletedWithAssociations(
            @Param("parentRoleId") Long parentRoleId,
            @Param("isDeleted") Integer isDeleted);

    /**
     * Checks if a role code exists (case-insensitive) excluding a specific role ID.
     *
     * @param roleCode the code to check
     * @param roleId   the ID to exclude
     * @return {@code true} if the code exists elsewhere
     */
    @Query("SELECT COUNT(r) > 0 FROM RbacRole r " +
            "WHERE LOWER(r.roleCode) = LOWER(:roleCode) AND r.roleId <> :roleId")
    boolean existsByRoleCodeIgnoreCase(
            @Param("roleCode") String roleCode,
            @Param("roleId") Long roleId);

    /**
     * Finds a role by name (case-insensitive) and soft-delete flag.
     * Efficient method for role lookup by name without full table scan.
     *
     * @param roleName  the role name to search for
     * @param isDeleted soft-delete flag (pass false for active records)
     * @return an Optional containing the role if found
     */
    Optional<RbacRole> findByRoleNameIgnoreCaseAndIsDeleted(String roleName, Integer isDeleted);

    /**
     * Returns all non-deleted role codes whose value starts with the given prefix pattern.
     * Used by the auto-generation logic to determine the next sequence number.
     *
     * <p>Pass the prefix followed by {@code %}, e.g. {@code "ADMIN%"}.
     *
     * @param pattern LIKE pattern (prefix + "%")
     * @return list of matching role codes
     */
    @Query("SELECT r.roleCode FROM RbacRole r WHERE r.roleCode LIKE :pattern AND r.isDeleted = 0")
    List<String> findRoleCodesByPrefixPattern(@Param("pattern") String pattern);

}
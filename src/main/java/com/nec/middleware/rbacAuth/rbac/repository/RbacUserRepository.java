package com.nec.middleware.rbacAuth.rbac.repository;

import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link RbacUser} (nec_rbac_users).
 *
 * <p>All finder methods automatically exclude soft-deleted records
 * (i.e. {@code is_deleted = false}).
 */
@Repository
public interface RbacUserRepository extends JpaRepository<RbacUser, String>,
        JpaSpecificationExecutor<RbacUser> {

    /**
     * Finds a non-deleted user by primary key.
     *
     * @param userId    the primary key
     * @param isDeleted soft-delete flag (pass {@code false} for active records)
     * @return an Optional containing the user if found
     */
    Optional<RbacUser> findByUserIdAndIsDeleted(String userId, Integer isDeleted);

    /**
     * Finds a non-deleted user by Keycloak subject UUID.
     */
    Optional<RbacUser> findByKeycloakUserIdAndIsDeleted(String keycloakUserId, Integer isDeleted);

    /**
     * Finds a non-deleted user by Keycloak subject UUID with the role association fetched.
     */
    @Query("SELECT u FROM RbacUser u "
            + "LEFT JOIN FETCH u.role "
            + "WHERE u.keycloakUserId = :keycloakUserId AND u.isDeleted = :isDeleted")
    Optional<RbacUser> findByKeycloakUserIdAndIsDeletedWithRole(
            @Param("keycloakUserId") String keycloakUserId,
            @Param("isDeleted") Integer isDeleted);

    /**
     * Finds a non-deleted user by primary key with all associations fetched.
     *
     * @param userId    the primary key
     * @param isDeleted soft-delete flag (pass {@code false} for active records)
     * @return an Optional containing the user with associations if found
     */
    @Query("SELECT u FROM RbacUser u "
            + "LEFT JOIN FETCH u.role "
            + "LEFT JOIN FETCH u.gender "
            + "LEFT JOIN FETCH u.department "
            + "LEFT JOIN FETCH u.region "
            + "LEFT JOIN FETCH u.district "
            + "LEFT JOIN FETCH u.city "
            + "WHERE u.userId = :userId AND u.isDeleted = :isDeleted")
    Optional<RbacUser> findByUserIdAndIsDeletedWithAssociations(
            @Param("userId") String userId,
            @Param("isDeleted") Integer isDeleted);

    /**
     * Returns all non-deleted users ordered by user name ascending.
     *
     * @param isDeleted soft-delete flag (pass {@code false})
     * @return list of active users
     */
    List<RbacUser> findAllByIsDeletedOrderByUserNameAsc(Integer isDeleted);

    @Query("SELECT DISTINCT u FROM RbacUser u "
            + "LEFT JOIN FETCH u.role "
            + "LEFT JOIN FETCH u.gender "
            + "LEFT JOIN FETCH u.department "
            + "LEFT JOIN FETCH u.region "
            + "LEFT JOIN FETCH u.district "
            + "LEFT JOIN FETCH u.city "
            + "WHERE u.isDeleted = :isDeleted ORDER BY u.userName")
    List<RbacUser> findAllByIsDeletedWithAssociations(@Param("isDeleted") Integer isDeleted);

    @Query("SELECT DISTINCT u FROM RbacUser u "
            + "LEFT JOIN FETCH u.role "
            + "LEFT JOIN FETCH u.gender "
            + "LEFT JOIN FETCH u.department "
            + "LEFT JOIN FETCH u.region "
            + "LEFT JOIN FETCH u.district "
            + "LEFT JOIN FETCH u.city "
            + "WHERE u.roleId = :roleId AND u.isDeleted = :isDeleted ORDER BY u.userName")
    List<RbacUser> findAllByRoleIdAndIsDeletedWithAssociations(
            @Param("roleId") Long roleId,
            @Param("isDeleted") Integer isDeleted);

    /**
     * Returns all non-deleted users with a specific active status.
     *
     * @param isActive  the active flag to filter by
     * @param isDeleted soft-delete flag (pass {@code false})
     * @return list of matching users
     */
    List<RbacUser> findAllByIsActiveAndIsDeletedOrderByUserNameAsc(Integer isActive, Integer isDeleted);

    /**
     * Returns all non-deleted users belonging to a specific role.
     *
     * @param roleId    the role ID to filter by
     * @param isDeleted soft-delete flag (pass {@code false})
     * @return list of matching users
     */
    List<RbacUser> findAllByRoleIdAndIsDeletedOrderByUserNameAsc(Long roleId, Integer isDeleted);

    /**
     * Checks whether a phone number already exists (used during creation).
     *
     * @param phone     the phone to check
     * @param isDeleted soft-delete flag (pass {@code false})
     * @return {@code true} if the phone exists in an active record
     */
    boolean existsByPhoneAndIsDeleted(String phone, Integer isDeleted);

    /**
     * Finds a non-deleted user by email address (case-insensitive).
     *
     * @param email     the email address to search for
     * @param isDeleted soft-delete flag (pass {@code false} for active records)
     * @return an Optional containing the user if found
     */
    Optional<RbacUser> findByEmailIgnoreCaseAndIsDeleted(String email, Integer isDeleted);

    /**
     * Checks whether an email address already exists (used during creation).
     *
     * @param email     the email to check
     * @param isDeleted soft-delete flag (pass {@code false})
     * @return {@code true} if the email exists in an active record
     */
    boolean existsByEmailIgnoreCaseAndIsDeleted(String email, Integer isDeleted);

    /**
     * Checks whether a phone number already exists, excluding a specific user ID
     * (used for update duplicate checks).
     *
     * @param phone  the phone to check
     * @param userId the user ID to exclude
     * @return {@code true} if a duplicate exists
     */
    @Query("SELECT COUNT(u) > 0 FROM RbacUser u " +
           "WHERE u.phone = :phone AND u.userId <> :userId AND u.isDeleted = 0")
    boolean existsByPhoneAndUserIdNot(@Param("phone") String phone, @Param("userId") String userId);

    /**
     * Checks whether an email already exists, excluding a specific user ID
     * (used for update duplicate checks).
     *
     * @param email  the email to check
     * @param userId the user ID to exclude
     * @return {@code true} if a duplicate exists
     */
    @Query("SELECT COUNT(u) > 0 FROM RbacUser u " +
           "WHERE LOWER(u.email) = LOWER(:email) AND u.userId <> :userId AND u.isDeleted = 0")
    boolean existsByEmailIgnoreCaseAndUserIdNot(@Param("email") String email, @Param("userId") String userId);
}



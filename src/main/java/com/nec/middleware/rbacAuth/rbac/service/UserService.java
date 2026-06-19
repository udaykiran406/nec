package com.nec.middleware.rbacAuth.rbac.service;



import com.nec.middleware.rbacAuth.rbac.dto.request.RbacUserRequest;

import com.nec.middleware.rbacAuth.rbac.dto.request.UserListRequestDto;

import com.nec.middleware.rbacAuth.rbac.dto.response.RbacUserResponse;

import com.nec.middleware.rbacAuth.rbac.dto.response.PaginatedResponse;



import java.util.List;



/**

 * Service interface for User-related operations.

 * Handles user CRUD operations, status management, and role-based queries.

 */

public interface UserService {




    /**
     * Restores a soft-deleted user (is_deleted = false) and re-enables them in Keycloak.
     *
     * @param id the primary key of the user
     * @return the restored user as a response DTO
     */
    RbacUserResponse restoreUser(String id);
    /**

     * Creates a new user record (INSERT).

     */

    RbacUserResponse createUser(RbacUserRequest request);



    /**

     * Updates an existing user record (UPDATE).

     *

     * @param id      the primary key of the user (path variable)

     * @param request the updated user data

     */

    RbacUserResponse updateUser(String id, RbacUserRequest request);



    /**

     * Retrieves a single non-deleted user by its primary key.

     */

    RbacUserResponse getUserById(String id);



    /**

     * Retrieves all non-deleted users, ordered alphabetically by user name.

     */

    List<RbacUserResponse> getAllUsers();



    /**

     * Retrieves users using optional pagination, sorting, search, and dynamic filters.

     *

     * @param request the user list request DTO

     * @return paginated or full user list response

     */

    PaginatedResponse<RbacUserResponse> listUsers(UserListRequestDto request);



    /**

     * Retrieves all non-deleted users belonging to a specific role.

     */

    List<RbacUserResponse> getUsersByRoleId(Long roleId);



    /**

     * Changes the {@code is_active} flag of a user.

     *

     * @param id       the primary key of the user

     * @param isActive the new active status

     * @return the updated user as a response DTO

     */

    RbacUserResponse changeUserActiveStatus(String id, Integer isActive);



    /**

     * Soft-deletes a user (is_deleted = true). Row is NOT physically removed.

     *

     * @param id the primary key of the user

     */

    void deleteUser(String id);

}




package com.nec.middleware.rbacAuth.rbac.constant;

/**
 * Application-level constants for the RBAC Roles module.
 *
 * <p>All message strings are centralised here so that they can be reused
 * across service, exception, and controller layers without duplication.
 */
public final class RbacConstants {

    // Prevent instantiation of this utility constant class
    private RbacConstants() {
        throw new UnsupportedOperationException("RbacConstants is a utility class");
    }

    // -------------------------------------------------------------------------
    // Success messages
    // -------------------------------------------------------------------------

    /** Message returned when a new role is successfully created. */
    public static final String ROLE_CREATED = "Role created successfully";

    /** Message returned when an existing role is successfully updated. */
    public static final String ROLE_UPDATED = "Role updated successfully";

    /** Message returned when a role is successfully soft-deleted. */
    public static final String ROLE_DELETED = "Role deleted successfully";

    /** Message returned when a role's status is successfully changed. */
    public static final String ROLE_STATUS_CHANGED = "Role status changed successfully";

    /** Message returned when a role is fetched successfully. */
    public static final String ROLE_FETCHED = "Role fetched successfully";

    /** Message returned when all roles are fetched successfully. */
    public static final String ROLES_FETCHED = "Roles fetched successfully";

    // -------------------------------------------------------------------------
    // Error / validation messages
    // -------------------------------------------------------------------------

    /** Message returned when no role is found for the given ID. */
    public static final String ROLE_NOT_FOUND = "Role not found with id: ";

    /** Message returned when a role with the same code already exists. */
    public static final String DUPLICATE_ROLE_CODE = "A role with this code already exists: ";

    /** Message returned when a role with the same name already exists. */
    public static final String DUPLICATE_ROLE_NAME = "A role with this name already exists: ";

    /** Message returned when the status value is invalid. */
    public static final String INVALID_STATUS =
            "Invalid status value. Allowed values are 'ACTIVE' and 'INACTIVE'";

    /** Message returned when the request payload itself is null. */
    public static final String REQUEST_NULL = "Request body must not be null";

    /** Message returned when approval limit is negative. */
    public static final String INVALID_APPROVAL_LIMIT = "Approval limit must be null or a non-negative value";

    /** Message returned when parent role cannot reference itself. */
    public static final String INVALID_PARENT_ROLE = "A role cannot be its own parent";

    /** Message returned when parent role is not found. */
    public static final String PARENT_ROLE_NOT_FOUND = "Parent role not found with id: ";

    /** Message returned when role code is blank or invalid. */
    public static final String INVALID_ROLE_CODE = "Role code must not be blank and must be between 1 and 50 characters";

    /** Message returned when role name is blank or invalid. */
    public static final String INVALID_ROLE_NAME = "Role name must not be blank and must be between 1 and 100 characters";

    /** Message returned when a user's active status is successfully changed. */
    public static final String USER_STATUS_CHANGED = "User active status changed successfully";

    // -------------------------------------------------------------------------
    // Module (nec_rbac_module) success messages
    // -------------------------------------------------------------------------

    /** Message returned when a new module is successfully created. */
    public static final String MODULE_CREATED = "Module created successfully";

    /** Message returned when an existing module is successfully updated. */
    public static final String MODULE_UPDATED = "Module updated successfully";

    /** Message returned when a module is successfully soft-deleted. */
    public static final String MODULE_DELETED = "Module deleted successfully";

    /** Message returned when a module is fetched successfully. */
    public static final String MODULE_FETCHED = "Module fetched successfully";

    /** Message returned when all modules are fetched successfully. */
    public static final String MODULES_FETCHED = "Modules fetched successfully";

    // -------------------------------------------------------------------------
    // Active / soft-delete flags (Integer: 1 = active / deleted, 0 = inactive / not deleted)
    // -------------------------------------------------------------------------

    /** Integer flag: record is active. */
    public static final Integer IS_ACTIVE_TRUE = 1;

    /** Integer flag: record is inactive. */
    public static final Integer IS_ACTIVE_FALSE = 0;

    /** Integer flag: record is not soft-deleted. */
    public static final Integer IS_DELETED_FALSE = 0;

    /** Integer flag: record has been soft-deleted. */
    public static final Integer IS_DELETED_TRUE = 1;

    // -------------------------------------------------------------------------
    // Allowed status values
    // -------------------------------------------------------------------------

    /** Allowed status: ACTIVE. */
    public static final String STATUS_ACTIVE = "ACTIVE";

    /** Allowed status: INACTIVE. */
    public static final String STATUS_INACTIVE = "INACTIVE";

    // -------------------------------------------------------------------------
    // User (nec_rbac_users) success messages
    // -------------------------------------------------------------------------

    /** Message returned when a new user is successfully created. */
    public static final String USER_CREATED = "User created successfully";

    /** Message returned when an existing user is successfully updated. */
    public static final String USER_UPDATED = "User updated successfully";

    /** Message returned when a user is successfully soft-deleted. */
    public static final String USER_DELETED = "User deleted successfully";

    /** Message returned when a soft-deleted user is successfully restored. */
    public static final String USER_RESTORED = "User restored successfully";

    /** Message returned when a user is fetched successfully. */
    public static final String USER_FETCHED = "User fetched successfully";

    /** Message returned when all users are fetched successfully. */
    public static final String USERS_FETCHED = "Users fetched successfully";

    // -------------------------------------------------------------------------
    // User (nec_rbac_users) error messages
    // -------------------------------------------------------------------------

    /** Message returned when no user is found for the given ID. */
    public static final String USER_NOT_FOUND = "User not found with id: ";

    /** Message returned when a user with the same phone already exists. */
    public static final String DUPLICATE_USER_PHONE = "A user with this phone already exists: ";

    /** Message returned when a user with the same email already exists. */
    public static final String DUPLICATE_USER_EMAIL = "A user with this email already exists: ";

    /** Message returned when isActive flag is not provided for status change. */
    public static final String USER_ACTIVE_STATUS_REQUIRED = "isActive flag must not be null";

    // -------------------------------------------------------------------------
    // Module (nec_rbac_module) error messages
    // -------------------------------------------------------------------------

    /** Message returned when no module is found for the given ID. */
    public static final String MODULE_NOT_FOUND = "Module not found with id: ";

    /** Message returned when a module with the same code already exists. */
    public static final String DUPLICATE_MODULE_CODE = "A module with this code already exists: ";

    /** Message returned when module code is blank or invalid. */
    public static final String INVALID_MODULE_CODE = "Module code must not be blank and must be between 1 and 50 characters";

    /** Message returned when module name is blank or invalid. */
    public static final String INVALID_MODULE_NAME = "Module name must not be blank and must be between 1 and 100 characters";

    /** Message returned when display order is invalid. */
    public static final String INVALID_DISPLAY_ORDER = "Display order must be a non-negative integer";

    // -------------------------------------------------------------------------
    // Permission Group (Module, Group, Permission) success messages
    // -------------------------------------------------------------------------

    /** Message returned when permission groups are saved successfully. */
    public static final String PERMISSION_GROUP_SAVED_SUCCESS = "Modules along with permission groups and permissions added successfully";

    /** Message returned when permission group data is fetched successfully. */
    public static final String PERMISSION_GROUP_FETCHED = "Permission groups fetched successfully";

    /** Message returned when permission group data is invalid. */
    public static final String PERMISSION_GROUP_INVALID = "Invalid permission group data";

    /** Message returned when transaction fails during permission group save. */
    public static final String PERMISSION_GROUP_SAVE_FAILED = "Failed to save permission groups";

    // -------------------------------------------------------------------------
    // Hierarchy (Module, Group, Permission) error messages
    // -------------------------------------------------------------------------

    /** Message returned when modules list is empty. */
    public static final String MODULES_LIST_EMPTY = "Modules list cannot be empty";

    /** Message returned when groups list is empty. */
    public static final String GROUPS_LIST_EMPTY = "Groups list cannot be empty";

    /** Message returned when permissions list is empty. */
    public static final String PERMISSIONS_LIST_EMPTY = "Permissions list cannot be empty";

    /** Message returned when module code is invalid. */
    public static final String INVALID_MODULE_CODE_HIERARCHY = "Module code must not be blank and must be between 1 and 50 characters";

    /** Message returned when group code is invalid. */
    public static final String INVALID_GROUP_CODE_HIERARCHY = "Group code must not be blank and must be between 1 and 50 characters";

     /** Message returned when permission code is invalid. */
     public static final String INVALID_PERMISSION_CODE_HIERARCHY = "Permission code must not be blank and must be between 1 and 50 characters";

     /** Message returned when a permission group with the same code already exists. */
     public static final String DUPLICATE_GROUP_CODE = "A permission group with this code already exists: ";

     /** Message returned when a permission with the same code already exists. */
     public static final String DUPLICATE_PERMISSION_CODE = "A permission with this code already exists: ";

 }

package com.nec.middleware.rbacAuth.rbac.constant;

/**
 * Constants for Role Permission Mapping module.
 *
 * <p>All message strings are centralised here for reuse across
 * service, exception, and controller layers without duplication.
 */
public final class RbacRolePermissionConstants {

    // Prevent instantiation of this utility constant class
    private RbacRolePermissionConstants() {
        throw new UnsupportedOperationException("RbacRolePermissionConstants is a utility class");
    }

    // -------------------------------------------------------------------------
    // Success messages
    // -------------------------------------------------------------------------

    /** Message returned when role permissions are successfully saved. */
    public static final String ROLE_PERMISSION_SAVED = "Role permissions mapped successfully";

    /** Message returned when role permission is successfully updated. */
    public static final String ROLE_PERMISSION_UPDATED = "Role permission updated successfully";

    /** Message returned when role permission is successfully soft-deleted. */
    public static final String ROLE_PERMISSION_DELETED = "Role permission deleted successfully";

    /** Message returned when role permission is successfully fetched. */
    public static final String ROLE_PERMISSION_FETCHED = "Role permission fetched successfully";

    /** Message returned when all role permissions are successfully fetched. */
    public static final String ROLE_PERMISSIONS_FETCHED = "Role permissions fetched successfully";

    // -------------------------------------------------------------------------
    // Validation error messages - Missing/Empty
    // -------------------------------------------------------------------------

    /** Message returned when role ID is missing. */
    public static final String ROLE_ID_MISSING = "Role ID is missing.";

    /** Message returned when role name is missing. */
    public static final String ROLE_NAME_MISSING = "Role Name is missing.";

    /** Message returned when module ID is missing. */
    public static final String MODULE_ID_MISSING = "Module Id is missing.";

    /** Message returned when group ID is missing. */
    public static final String GROUP_ID_MISSING = "Group Id is missing.";

    /** Message returned when permission ID is missing. */
    public static final String PERMISSION_ID_MISSING = "Permission Id is missing.";

    /** Message returned when module list is missing. */
    public static final String MODULE_LIST_MISSING = "Module list is missing.";

    /** Message returned when group list is missing. */
    public static final String GROUP_LIST_MISSING = "Group list is missing.";

    /** Message returned when permission list is missing. */
    public static final String PERMISSION_LIST_MISSING = "Permission list is missing.";

    // -------------------------------------------------------------------------
    // Validation error messages - Invalid/Not Found
    // -------------------------------------------------------------------------

    /** Message returned when the specified role is not found. */
    public static final String INVALID_ROLE = "Invalid Role.";

    /** Message returned when the specified module is not found. */
    public static final String INVALID_MODULE = "Invalid Module.";

    /** Message returned when the specified group is not found. */
    public static final String INVALID_GROUP = "Invalid Group.";

    /** Message returned when the specified permission is not found. */
    public static final String INVALID_PERMISSION = "Invalid Permission.";

    /** Message returned when permission does not belong to the selected group. */
    public static final String PERMISSION_NOT_IN_GROUP = "Permission does not belong to the selected Group.";

    /** Message returned when group does not belong to the selected module. */
    public static final String GROUP_NOT_IN_MODULE = "Group does not belong to the selected Module.";

    // -------------------------------------------------------------------------
    // Duplicate/Conflict error messages
    // -------------------------------------------------------------------------

    /** Message returned when permission is already mapped to the role. */
    public static final String PERMISSION_ALREADY_MAPPED = "Permission already mapped to this role.";

    // -------------------------------------------------------------------------
    // Not Found error messages
    // -------------------------------------------------------------------------

    /** Message returned when the role-permission mapping is not found. */
    public static final String ROLE_PERMISSION_NOT_FOUND = "Role permission mapping not found.";
}


package com.nec.middleware.rbacAuth.rbac.constant;

/**
 * API response messages for the NEC Middleware application.
 *
 * Centralized collection of all user-facing messages used in API responses.
 * Ensures consistency and makes it easy to maintain messaging across the application.
 *
 * <p>All messages are designed to be:
 * - User-friendly (no technical jargon)
 * - Clear and actionable
 * - Free from internal implementation details
 *
 * <p>Usage:
 * <pre>
 * return ResponseEntity.ok(
 *     ApiResponse.created(ApiMessageConstants.ROLE_CREATED_SUCCESSFULLY)
 * );
 * </pre>
 */
public class ApiMessageConstants {

    // Prevent instantiation
    private ApiMessageConstants() {
        throw new AssertionError("Cannot instantiate ApiMessageConstants");
    }

    /* ============================================
       SUCCESS MESSAGES
       ============================================ */

    // General
    public static final String OPERATION_SUCCESSFUL = "Operation completed successfully.";
    public static final String INFORMATION_RETRIEVED = "Information retrieved successfully.";

    // User-related
    public static final String USER_CREATED_SUCCESSFULLY = "User created successfully.";
    public static final String USER_UPDATED_SUCCESSFULLY = "User updated successfully.";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully.";
    public static final String USER_RETRIEVED_SUCCESSFULLY = "User information retrieved successfully.";
    public static final String USERS_RETRIEVED_SUCCESSFULLY = "Users retrieved successfully.";

    // Role-related
    public static final String ROLE_CREATED_SUCCESSFULLY = "Role created successfully.";
    public static final String ROLE_UPDATED_SUCCESSFULLY = "Role updated successfully.";
    public static final String ROLE_DELETED_SUCCESSFULLY = "Role deleted successfully.";
    public static final String ROLE_RETRIEVED_SUCCESSFULLY = "Role information retrieved successfully.";
    public static final String ROLES_RETRIEVED_SUCCESSFULLY = "Roles retrieved successfully.";

    // Module-related
    public static final String MODULE_CREATED_SUCCESSFULLY = "Module created successfully.";
    public static final String MODULE_UPDATED_SUCCESSFULLY = "Module updated successfully.";
    public static final String MODULE_DELETED_SUCCESSFULLY = "Module deleted successfully.";
    public static final String MODULE_RETRIEVED_SUCCESSFULLY = "Module information retrieved successfully.";
    public static final String MODULES_RETRIEVED_SUCCESSFULLY = "Modules retrieved successfully.";

    // Permission Group-related
    public static final String GROUP_CREATED_SUCCESSFULLY = "Permission group created successfully.";
    public static final String GROUP_UPDATED_SUCCESSFULLY = "Permission group updated successfully.";
    public static final String GROUP_DELETED_SUCCESSFULLY = "Permission group deleted successfully.";
    public static final String GROUP_RETRIEVED_SUCCESSFULLY = "Permission group information retrieved successfully.";
    public static final String GROUPS_RETRIEVED_SUCCESSFULLY = "Permission groups retrieved successfully.";

    // Permission-related
    public static final String PERMISSION_CREATED_SUCCESSFULLY = "Permission created successfully.";
    public static final String PERMISSION_UPDATED_SUCCESSFULLY = "Permission updated successfully.";
    public static final String PERMISSION_DELETED_SUCCESSFULLY = "Permission deleted successfully.";
    public static final String PERMISSION_RETRIEVED_SUCCESSFULLY = "Permission information retrieved successfully.";
    public static final String PERMISSIONS_RETRIEVED_SUCCESSFULLY = "Permissions retrieved successfully.";

    // Module Hierarchy-related
    public static final String MODULE_HIERARCHY_CREATED_SUCCESSFULLY = "Module hierarchy created successfully.";
    public static final String MODULE_HIERARCHY_UPDATED_SUCCESSFULLY = "Module hierarchy updated successfully.";
    public static final String MODULE_HIERARCHY_RETRIEVED_SUCCESSFULLY = "Module hierarchy retrieved successfully.";

    /* ============================================
       ERROR MESSAGES - VALIDATION
       ============================================ */

    public static final String VALIDATION_FAILED = "Please review the information you entered.";
    public static final String REQUIRED_FIELD_MISSING = "One or more mandatory fields are missing.";
    public static final String INVALID_DATA_FORMAT = "The submitted information is not in the correct format.";
    public static final String INVALID_INPUT = "The provided information is invalid.";

    /* ============================================
       ERROR MESSAGES - DUPLICATE/CONFLICT
       ============================================ */

    public static final String DUPLICATE_RECORD = "A record with the same information already exists.";
    public static final String DUPLICATE_EMAIL = "A user with this email already exists.";
    public static final String DUPLICATE_PHONE = "A user with this phone number already exists.";
    public static final String DUPLICATE_CODE = "This code already exists.";
    public static final String DUPLICATE_NAME = "This name already exists.";
    public static final String DUPLICATE_ROLE_CODE = "A role with this code already exists.";
    public static final String DUPLICATE_ROLE_NAME = "A role with this name already exists.";
    public static final String DUPLICATE_MODULE_CODE = "A module with this code already exists.";
    public static final String DUPLICATE_GROUP_CODE = "A permission group with this code already exists.";
    public static final String DUPLICATE_PERMISSION_CODE = "A permission with this code already exists.";

    /* ============================================
       ERROR MESSAGES - NOT FOUND
       ============================================ */

    public static final String NOT_FOUND = "The requested information could not be found.";
    public static final String USER_NOT_FOUND = "The requested user could not be found.";
    public static final String ROLE_NOT_FOUND = "The requested role could not be found.";
    public static final String MODULE_NOT_FOUND = "The requested module could not be found.";
    public static final String GROUP_NOT_FOUND = "The requested permission group could not be found.";
    public static final String PERMISSION_NOT_FOUND = "The requested permission could not be found.";

    /* ============================================
       ERROR MESSAGES - REFERENCE/CONSTRAINT
       ============================================ */

    public static final String INVALID_REFERENCE = "The selected reference is not valid.";
    public static final String PARENT_ROLE_NOT_FOUND = "The selected parent role does not exist.";
    public static final String USER_ROLE_ASSIGNMENT_FAILED = "Could not assign role to user.";
    public static final String INVALID_STATUS_VALUE = "The status value is invalid.";

    /* ============================================
       ERROR MESSAGES - BUSINESS LOGIC
       ============================================ */

    public static final String BUSINESS_RULE_VIOLATION = "The provided information does not meet the required criteria.";
    public static final String CANNOT_DELETE_ROLE = "This role cannot be deleted. It is in use.";
    public static final String CANNOT_DELETE_MODULE = "This module cannot be deleted. It contains active permissions.";
    public static final String CANNOT_DELETE_ACTIVE_RECORD = "You cannot delete an active record. Please mark it as inactive first.";
    public static final String CANNOT_MODIFY_SYSTEM_RECORD = "You cannot modify system records.";

    /* ============================================
       ERROR MESSAGES - DATABASE/CONCURRENCY
       ============================================ */

    public static final String DATABASE_ERROR = "We could not process your request at this time. Please try again later.";
    public static final String CONCURRENT_UPDATE = "The information was updated by another user. Please refresh and try again.";
    public static final String DATA_INTEGRITY_ERROR = "A data conflict occurred. Please check your input and try again.";

    /* ============================================
       ERROR MESSAGES - UNEXPECTED/GENERIC
       ============================================ */

    public static final String UNEXPECTED_ERROR = "An unexpected error occurred. Please contact support if the problem persists.";
    public static final String REQUEST_PROCESSING_ERROR = "An error occurred while processing your request.";
    public static final String SERVICE_UNAVAILABLE = "The service is temporarily unavailable. Please try again later.";

    /* ============================================
       FIELD-SPECIFIC ERROR MESSAGES
       ============================================ */

    // Module fields
    public static final String MODULE_CODE_REQUIRED = "Module code is required.";
    public static final String MODULE_CODE_INVALID = "Module code can contain only uppercase letters, numbers, and underscores.";
    public static final String MODULE_NAME_REQUIRED = "Module name is required.";
    public static final String MODULE_DISPLAY_ORDER_REQUIRED = "Display order is required.";
    public static final String MODULE_STATUS_REQUIRED = "Status is required.";
    public static final String MODULE_CREATED_BY_REQUIRED = "Created by user ID is required.";

    // Group fields
    public static final String GROUP_CODE_REQUIRED = "Group code is required.";
    public static final String GROUP_CODE_INVALID = "Group code can contain only uppercase letters, numbers, and underscores.";
    public static final String GROUP_NAME_REQUIRED = "Group name is required.";
    public static final String GROUP_DISPLAY_ORDER_REQUIRED = "Display order is required.";
    public static final String GROUP_STATUS_REQUIRED = "Status is required.";
    public static final String GROUP_CREATED_BY_REQUIRED = "Created by user ID is required.";

    // Permission fields
    public static final String PERMISSION_CODE_REQUIRED = "Permission code is required.";
    public static final String PERMISSION_CODE_INVALID = "Permission code can contain only uppercase letters, numbers, and underscores.";
    public static final String PERMISSION_NAME_REQUIRED = "Permission name is required.";
    public static final String PERMISSION_DISPLAY_ORDER_REQUIRED = "Display order is required.";
    public static final String PERMISSION_STATUS_REQUIRED = "Status is required.";
    public static final String PERMISSION_CREATED_BY_REQUIRED = "Created by user ID is required.";

    // User fields
    public static final String USER_EMAIL_REQUIRED = "Email is required.";
    public static final String USER_EMAIL_INVALID = "Please enter a valid email address.";
    public static final String USER_PHONE_REQUIRED = "Phone number is required.";
    public static final String USER_PHONE_INVALID = "Please enter a valid phone number.";
    public static final String USER_FIRST_NAME_REQUIRED = "First name is required.";
    public static final String USER_LAST_NAME_REQUIRED = "Last name is required.";

    // Role fields
    public static final String ROLE_CODE_REQUIRED = "Role code is required.";
    public static final String ROLE_NAME_REQUIRED = "Role name is required.";
    public static final String ROLE_STATUS_REQUIRED = "Status is required.";

    /* ============================================
       ACTION-SPECIFIC MESSAGES
       ============================================ */

    public static final String PROVIDE_MODULE_CODE = "Please provide a module code.";
    public static final String PROVIDE_GROUP_CODE = "Please provide a group code.";
    public static final String PROVIDE_PERMISSION_CODE = "Please provide a permission code.";
    public static final String PROVIDE_USER_EMAIL = "Please provide a user email.";
    public static final String PROVIDE_ROLE_CODE = "Please provide a role code.";

    public static final String CHECK_DISPLAY_ORDER = "Please enter a valid display order.";
    public static final String CHECK_STATUS = "Please select a valid status.";
    public static final String CHECK_USER_ID = "Please enter a valid user ID.";

    /* ============================================
       PAGINATION MESSAGES
       ============================================ */

    public static final String NO_RECORDS_FOUND = "No records found.";
    public static final String RECORDS_RETRIEVED = "Records retrieved successfully.";
    public static final String DATA_FETCHED = "Data fetched successfully.";

    /* ============================================
       FILTER/SEARCH MESSAGES
       ============================================ */

    public static final String FILTER_APPLIED_SUCCESSFULLY = "Filter applied successfully.";
    public static final String SEARCH_COMPLETED = "Search completed.";
    public static final String NO_MATCHING_RECORDS = "No records match your search criteria.";
}


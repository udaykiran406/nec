package com.nec.middleware.exception;

import com.nec.middleware.rbacAuth.rbac.constant.ErrorCodeConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Maps database constraint names to field names and user-friendly messages.
 * Provides centralized mapping for unique constraints across the application.
 */
public class ConstraintFieldMapper {

    private static final Pattern DUPLICATE_KEY_VALUE_PATTERN = Pattern.compile(
            "key\\s*\\([^)]+\\)\\s*=\\s*\\(([^)]+)\\)",
            Pattern.CASE_INSENSITIVE);

    private static final Map<String, ConstraintMapping> CONSTRAINT_MAPPINGS = new HashMap<>();

    static {
        // User constraints
        CONSTRAINT_MAPPINGS.put("uq_nec_rbac_users_phone",
                new ConstraintMapping("Phone Number",
                        "Phone number '%s' already exists.",
                        "Phone number already exists."));
        CONSTRAINT_MAPPINGS.put("uq_nec_rbac_users_email",
                new ConstraintMapping("Email",
                        "Email '%s' already exists.",
                        "Email already exists."));

        // Role constraints
        CONSTRAINT_MAPPINGS.put("uq_nec_rbac_roles_code",
                new ConstraintMapping("Role Code",
                        "Role code '%s' already exists.",
                        "Role code already exists."));
        CONSTRAINT_MAPPINGS.put("uq_nec_rbac_roles_name",
                new ConstraintMapping("Role Name",
                        "Role name '%s' already exists.",
                        "Role name already exists."));

        // Module constraints
        CONSTRAINT_MAPPINGS.put("uq_nec_rbac_module_code",
                new ConstraintMapping("Module Code",
                        "Module code '%s' already exists.",
                        "Module code already exists."));

        // Permission Group constraints
        CONSTRAINT_MAPPINGS.put("uq_rbac_group_code",
                new ConstraintMapping("Group Code",
                        "Group code '%s' already exists.",
                        "Group code already exists."));

        // Permission constraints
        CONSTRAINT_MAPPINGS.put("uq_rbac_permission_code",
                new ConstraintMapping("Permission Code",
                        "Permission code '%s' already exists.",
                        "Permission code already exists."));

        // Role-permission mapping
        CONSTRAINT_MAPPINGS.put("uq_rbac_role_permission",
                new ConstraintMapping("Role Permission",
                        "This permission is already mapped to the role.",
                        "This permission is already mapped to the role."));
    }

    public static class ConstraintMapping {
        private final String fieldName;
        private final String messageWithValueTemplate;
        private final String messageWithoutValue;

        public ConstraintMapping(String fieldName, String messageWithValueTemplate, String messageWithoutValue) {
            this.fieldName = fieldName;
            this.messageWithValueTemplate = messageWithValueTemplate;
            this.messageWithoutValue = messageWithoutValue;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getMessageWithValueTemplate() {
            return messageWithValueTemplate;
        }

        public String getMessageWithoutValue() {
            return messageWithoutValue;
        }
    }

    /**
     * Gets the constraint mapping for a given constraint name.
     *
     * @param constraintName the database constraint name (e.g., uq_nec_rbac_users_phone)
     * @return the constraint mapping or null if not found
     */
    public static ConstraintMapping getMapping(String constraintName) {
        if (constraintName == null) {
            return null;
        }
        return CONSTRAINT_MAPPINGS.get(constraintName.toLowerCase());
    }

    /**
     * Builds a user-friendly duplicate message for a known constraint and submitted value.
     *
     * @param constraintName the database constraint name
     * @param duplicateValue   the duplicate business value, when known
     * @return a sanitized user-facing message
     */
    public static String formatDuplicateMessage(String constraintName, String duplicateValue) {
        ConstraintMapping mapping = getMapping(constraintName);
        if (mapping == null) {
            return ErrorCodeConstants.DUPLICATE_RECORD_MSG;
        }
        if (duplicateValue != null && !duplicateValue.isBlank()) {
            return String.format(mapping.getMessageWithValueTemplate(), duplicateValue.trim());
        }
        return mapping.getMessageWithoutValue();
    }

    /**
     * Resolves a user-friendly duplicate message from a database/Hibernate error message.
     *
     * @param errorMessage combined error text from the exception chain
     * @return user-friendly message or null if the constraint is not recognized
     */
    public static String resolveDuplicateMessage(String errorMessage) {
        String constraintName = extractConstraintName(errorMessage);
        if (constraintName == null) {
            return null;
        }
        String duplicateValue = extractDuplicateValue(errorMessage);
        return formatDuplicateMessage(constraintName, duplicateValue);
    }

    /**
     * Extracts constraint name from error message patterns.
     *
     * @param errorMessage the error message from the database
     * @return the constraint name or null if not found
     */
    public static String extractConstraintName(String errorMessage) {
        if (errorMessage == null) {
            return null;
        }

        String lowerMessage = errorMessage.toLowerCase();

        for (String constraint : CONSTRAINT_MAPPINGS.keySet()) {
            if (lowerMessage.contains(constraint)) {
                return constraint;
            }
        }

        return null;
    }

    /**
     * Extracts the duplicate business value from PostgreSQL-style detail messages.
     * Example: {@code Key (module_code)=(HR) already exists.}
     *
     * @param errorMessage the database error message
     * @return the duplicate value or null if it cannot be determined safely
     */
    public static String extractDuplicateValue(String errorMessage) {
        if (errorMessage == null || errorMessage.isBlank()) {
            return null;
        }

        Matcher matcher = DUPLICATE_KEY_VALUE_PATTERN.matcher(errorMessage);
        if (matcher.find()) {
            String value = matcher.group(1).trim();
            if (isSafeBusinessValue(value)) {
                return value;
            }
        }

        return null;
    }

    /**
     * Gets user-friendly message for a constraint violation.
     *
     * @param errorMessage the database error message
     * @return user-friendly error message or null if constraint not recognized
     */
    public static String getUserFriendlyMessage(String errorMessage) {
        return resolveDuplicateMessage(errorMessage);
    }

    /**
     * Gets field name for a constraint violation.
     *
     * @param errorMessage the database error message
     * @return field name or null if constraint not recognized
     */
    public static String getFieldName(String errorMessage) {
        String constraintName = extractConstraintName(errorMessage);
        if (constraintName != null) {
            ConstraintMapping mapping = getMapping(constraintName);
            if (mapping != null) {
                return mapping.getFieldName();
            }
        }
        return null;
    }

    private static boolean isSafeBusinessValue(String value) {
        if (value.isBlank()) {
            return false;
        }
        String lower = value.toLowerCase();
        return !lower.contains("insert into")
                && !lower.contains("update ")
                && !lower.contains("delete from")
                && !lower.contains("constraint")
                && !lower.contains("sql")
                && value.length() <= 100;
    }

    private ConstraintFieldMapper() {
        throw new AssertionError("Cannot instantiate ConstraintFieldMapper");
    }
}

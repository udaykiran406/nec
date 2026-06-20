package com.nec.middleware.rbacAuth.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for user details from Keycloak.
 * Includes complete user information and custom attributes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String userId;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private Boolean enabled;

    private Boolean emailVerified;

    private Long createdTimestamp;

    /**
     * Custom attributes associated with the user.
     * Contains extended profile information like:
     * - department
     * - designation
     * - employeeCode
     * - branchCode
     * - nationalId
     *
     * Values are {@code List<String>} to comply with Keycloak's attribute storage model.
     * Returns empty map if no attributes are present (never null).
     */
    private Map<String, List<String>> attributes;
}


package com.nec.middleware.rbacAuth.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for creating a new user in Keycloak and the local database.
 * Supports custom attributes for extended user profile information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$", message = "Username must be at least 3 characters and contain only alphanumeric characters, dots, underscores, or hyphens")
    private String username;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    private Boolean enabled;

    private Boolean emailVerified;

    /**
     * Custom attributes to store with the user.
     * Supported keys:
     * - department
     * - designation
     * - employeeCode
     * - branchCode
     * - nationalId
     *
     * Values are stored as {@code List<String>} to comply with Keycloak's attribute storage model.
     * Example: {"department": ["IT"], "designation": ["Senior Developer"], "employeeCode": ["EMP001"]}
     */
    private Map<String, List<String>> attributes;
}


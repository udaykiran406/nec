package com.nec.middleware.rbacAuth.rbac.dto.request;

import com.nec.middleware.rbacAuth.rbac.validation.Phone;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO used for both CREATE and UPDATE operations on {@code nec_rbac_users}.
 *
 * <p>When {@code id} is {@code null} → INSERT; when non-null → UPDATE.
 *
 * <p>Sample JSON (create):
 * <pre>
 * {
 *   "userName": "John Doe",
 *   "genderId": 1,
 *   "roleId": 2,
 *   "phone": "+255700000001",
 *   "email": "john.doe@nec.go.tz",
 *   "photoPath": "/photos/john.jpg",
 *   "departmentId": 3,
 *   "regionId": 1,
 *   "districtId": 5,
 *   "cityId": 10,
 *   "password": "SecurePass123",
 *   "createdBy": 1
 * }
 * </pre>
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacUserRequest {

    /**
     * Required only during update.
     */
    private String id;

    @NotBlank(message = "User Name is required.")
    @Size(max = 150, message = "User Name must not exceed 150 characters.")
    private String userName;

    @NotNull(message = "Gender ID is required.")
    @Positive(message = "Gender ID must be a positive number.")
    private Long genderId;

    @NotNull(message = "Role ID is required.")
    @Positive(message = "Role ID must be a positive number.")
    private Long roleId;

    @NotBlank(message = "Phone Number is required.")
    @Phone(message = "Phone Number is not in a valid format.")
    @Size(max = 20, message = "Phone Number must not exceed 20 characters.")
    private String phone;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email is not in a valid format.")
    @Size(max = 150, message = "Email must not exceed 150 characters.")
    private String email;

    @Size(max = 500, message = "Photo Path must not exceed 500 characters.")
    private String photoPath;

    @NotNull(message = "Department ID is required.")
    @Positive(message = "Department ID must be a positive number.")
    private Long departmentId;

    @NotNull(message = "Region ID is required.")
    @Positive(message = "Region ID must be a positive number.")
    private Long regionId;

    @NotNull(message = "District ID is required.")
    @Positive(message = "District ID must be a positive number.")
    private Long districtId;

    @NotNull(message = "City ID is required.")
    @Positive(message = "City ID must be a positive number.")
    private Long cityId;

    /**
     * Required on create only. Plaintext initial password sent to Keycloak over TLS;
     * a BCrypt hash is computed server-side for the local {@code password_hash} column.
     */
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
    private String password;

    /**
     * If true, user will be forced to change password on next login.
     * Defaults to false.
     */
    @Builder.Default
    private Boolean passwordToBeChanged = false;

    /**
     * Whether the user's email has been verified.
     * Defaults to false.
     */
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * Whether the user's mobile number has been verified.
     * Defaults to false.
     */
    @Builder.Default
    private Boolean mobileVerified = false;

    /**
     * Default true if not supplied.
     */
    @Builder.Default
    private Integer isActive = 1;

    @NotNull(message = "Created By is required.")
    @Positive(message = "Created By must be a positive number.")
    private Long createdBy;

    @Positive(message = "Updated By must be a positive number.")
    private Long updatedBy;
}
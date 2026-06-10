package com.nec.middleware.portal.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortalUserRequestDto {

    // null → CREATE, non-null → UPDATE
    private Long id;

    @NotBlank(message = "User name is required")
    @Size(max = 150, message = "User name must not exceed 150 characters")
    private String userName;

    @NotNull(message = "Gender is required")
    private Long genderId;

    @NotNull(message = "Role is required")
    private Long roleId;

    @NotBlank(message = "Phone number is required")
    @Size(min = 9, max = 20, message = "Phone must be between 9 and 20 characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Phone must contain only digits and allowed symbols")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(max = 500, message = "Photo path must not exceed 500 characters")
    private String photoPath;

    @Size(max = 100, message = "Faculty must not exceed 100 characters")
    private String faculty;

    @NotNull(message = "Department is required")
    private Long departmentId;

    @NotNull(message = "Region is required")
    private Long regionId;

    @NotNull(message = "District is required")
    private Long districtId;

    @NotNull(message = "City is required")
    private Long cityId;

    private Long portalUserTypeId;

    private Long referenceId;

    // Audit — populated from security context in the service layer
    private Long createdBy;
    private Long updatedBy;
}

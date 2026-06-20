package com.nec.middleware.rbacAuth.rbac.dto.request;

import com.nec.middleware.template.ExcelColumn;
import com.nec.middleware.rbacAuth.rbac.validation.Phone;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Bulk upload request DTO for RbacUser.
 * <p>Sample Excel row:
 * <pre>
 *   User Name | Gender ID | Role ID | Phone | Email | Department ID | Region ID | District ID | City ID
 *   John Doe  | 1         | 2       | +255... | j@nec | 3 | 1 | 5 | 10
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacUserBulkUploadDto {

    @ExcelColumn(name = "User Name", mandatory = true, sample = "John Doe", description = "Full name of the user")
    @NotBlank(message = "User Name is required.")
    @Size(max = 150, message = "User Name must not exceed 150 characters.")
    private String userName;

    @ExcelColumn(name = "Gender ID", mandatory = true, sample = "1", description = "ID of gender from nec_lkp_genders table")
    @NotNull(message = "Gender ID is required.")
    @Positive(message = "Gender ID must be a positive number.")
    private Long genderId;

    @ExcelColumn(name = "Role ID", mandatory = true, sample = "2", description = "ID of role from nec_rbac_roles table")
    @NotNull(message = "Role ID is required.")
    @Positive(message = "Role ID must be a positive number.")
    private Long roleId;

    @ExcelColumn(name = "Phone", mandatory = true, sample = "+255700000001", description = "Unique phone number in valid format")
    @NotBlank(message = "Phone Number is required.")
    @Phone(message = "Phone Number is not in a valid format.")
    @Size(max = 20, message = "Phone Number must not exceed 20 characters.")
    private String phone;

    @ExcelColumn(name = "Email", mandatory = true, sample = "john.doe@nec.go.tz", description = "Unique email address")
    @NotBlank(message = "Email is required.")
    @Email(message = "Email is not in a valid format.")
    @Size(max = 150, message = "Email must not exceed 150 characters.")
    private String email;

    @ExcelColumn(name = "Photo Path", mandatory = false, sample = "/photos/john.jpg", description = "Optional path to user photo file")
    @Size(max = 500, message = "Photo Path must not exceed 500 characters.")
    private String photoPath;

    @ExcelColumn(name = "Department ID", mandatory = true, sample = "3", description = "ID of department from nec_lkp_departments table")
    @NotNull(message = "Department ID is required.")
    @Positive(message = "Department ID must be a positive number.")
    private Long departmentId;

    @ExcelColumn(name = "Region ID", mandatory = true, sample = "1", description = "ID of region from nec_regions table")
    @NotNull(message = "Region ID is required.")
    @Positive(message = "Region ID must be a positive number.")
    private Long regionId;

    @ExcelColumn(name = "District ID", mandatory = true, sample = "5", description = "ID of district from nec_districts table")
    @NotNull(message = "District ID is required.")
    @Positive(message = "District ID must be a positive number.")
    private Long districtId;

    @ExcelColumn(name = "City ID", mandatory = true, sample = "10", description = "ID of city from nec_cities table")
    @NotNull(message = "City ID is required.")
    @Positive(message = "City ID must be a positive number.")
    private Long cityId;

    @ExcelColumn(name = "Password To Be Changed", mandatory = false, sample = "false", description = "Optional: force user to change password on next login (true/false)")
    @Builder.Default
    private Boolean passwordToBeChanged = false;

    @ExcelColumn(name = "Is Active", mandatory = false, sample = "1", description = "Optional: user active status (1=active, 0=inactive). Defaults to 1")
    @Builder.Default
    private Integer isActive = 1;
}


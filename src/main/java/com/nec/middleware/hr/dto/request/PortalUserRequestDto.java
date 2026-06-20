package com.nec.middleware.hr.dto.request;

import com.nec.middleware.template.ExcelColumn;
import jakarta.validation.constraints.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortalUserRequestDto {



    private String portalUserId;

    @ExcelColumn(
            name = "User Name",
            mandatory = true,
            sample = "John Doe"
    )
    @NotBlank(message = "User name is required")
    @Size(max = 150, message = "User name must not exceed 150 characters")
    private String userName;

    @ExcelColumn(
            name = "Gender ID",
            mandatory = true,
            sample = "1",
            description = "Reference ID from gender lookup"
    )
    @NotNull(message = "Gender is required")
    private Long genderId;

    @ExcelColumn(
            name = "Role ID",
            mandatory = true,
            sample = "9",
            description = "Reference ID from role lookup"
    )
    @NotNull(message = "Role is required")
    private Long roleId;

    @ExcelColumn(
            name = "Phone",
            mandatory = true,
            sample = "+91 9876543210",
            description = "Digits and + - ( ) only"
    )
    @NotBlank(message = "Phone number is required")
    @Size(min = 9, max = 20, message = "Phone must be between 9 and 20 characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Phone must contain only digits and allowed symbols")
    private String phone;

    @ExcelColumn(
            name = "Email",
            mandatory = true,
            sample = "user@example.com"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @ExcelColumn(
            name = "Photo Path",
            sample = "uploads/photo.jpg",
            description = "Generated automatically after upload"
    )
    // Populated by the service layer after storing the uploaded multipart file.
    // Not settable by the client directly (no @Size/manual-entry validation needed).
    private String photoPath;

    @ExcelColumn(
            name = "Faculty",
            sample = "Computer Science"
    )
    @Size(max = 100, message = "Faculty must not exceed 100 characters")
    private String faculty;


    @ExcelColumn(
            name = "Region ID",
            mandatory = true,
            sample = "1",
            description = "Reference ID from region master"
    )
    @NotNull(message = "Region is required")
    private Long regionId;

    @ExcelColumn(
            name = "District ID",
            mandatory = true,
            sample = "2",
            description = "Reference ID from district master"
    )
    @NotNull(message = "District is required")
    private Long districtId;

    @ExcelColumn(
            name = "City ID",
            mandatory = true,
            sample = "6",
            description = "Reference ID from city master"
    )
    @NotNull(message = "City is required")
    private Long cityId;

    @ExcelColumn(
            name = "Portal User Type ID",
            mandatory = true,
            sample = "1",
            description = "Reference ID from portal user type lookup"
    )
    @NotNull(message = "Portal user type is required")
    private Long portalUserTypeId;

    @ExcelColumn(
            name = "Master Data ID",
            mandatory = true,
            sample = "1"
    )
    @NotNull(message = "MasterDataId is required")
    private Long masterdataId;


    // Audit — populated from security context in the service layer
    private String createdBy;
    private String updatedBy;
}

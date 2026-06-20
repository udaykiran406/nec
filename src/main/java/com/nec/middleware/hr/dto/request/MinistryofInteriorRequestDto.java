package com.nec.middleware.hr.dto.request;

import com.nec.middleware.template.ExcelColumn;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinistryofInteriorRequestDto {

    private String ministryofInteriorId;

    @ExcelColumn(
            name = "Title Type",
            mandatory = true,
            sample = "1",
            description = "Reference ID from title master table"
    )
    @NotNull(message = "Title type is required")
    private Long moiTitleId;

    @ExcelColumn(
            name = "Full Name",
            mandatory = true,
            sample = "John Doe"
    )
    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must not exceed 120 characters")
    private String Name;

    @ExcelColumn(
            name = "Gender ID",
            mandatory = true,
            sample = "1",
            description = "Reference ID from gender master table"
    )
    @NotNull(message = "Gender is required")
    private Long genderId;

    @ExcelColumn(
            name = "Age",
            mandatory = true,
            sample = "35",
            description = "Allowed range: 17–100"
    )
    @Min(value = 17, message = "Age must be at least 17")
    @Max(value = 100, message = "Age must not exceed 100")
    private Short age;

    @ExcelColumn(
            name = "Phone",
            mandatory = true,
            sample = "+91 9876543210",
            description = "Digits and + - ( ) only"
    )
    @NotBlank(message = "Phone number is required")
    @Size(min = 9, max = 30, message = "Phone must be between 9 and 30 characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Phone must contain only digits and allowed symbols")
    private String phone;

    @ExcelColumn(
            name = "Photo Path",
            sample = "uploads/photo.jpg",
            description = "Generated automatically after upload"
    )
    // Populated by the service layer after storing the uploaded multipart file.
    // Not settable by the client directly (no @Size/manual-entry validation needed).
    private String photoPath;

    @ExcelColumn(
            name = "Email",
            mandatory = true,
            sample = "user@example.com"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @ExcelColumn(
            name = "Region ID",
            mandatory = true,
            sample = "1",
            description = "Reference ID from region master table"
    )
    @NotNull(message = "Region is required")
    private Long regionId;

    @ExcelColumn(
            name = "District ID",
            mandatory = true,
            sample = "3",
            description = "Reference ID from district master table"
    )
    @NotNull(message = "District is required")
    private Long districtId;

    @ExcelColumn(
            name = "City ID",
            mandatory = true,
            sample = "10",
            description = "Reference ID from city master table"
    )
    @NotNull(message = "City is required")
    private Long cityId;

    @ExcelColumn(
            name = "VRC ID",
            mandatory = true,
            sample = "5",
            description = "Reference ID from voter registration center"
    )
    @NotNull(message = "Voter Registration Center is required")
    private Long vrcId;

    // Audit — populated from security context in the service layer
    private String createdBy;
    private String updatedBy;
}
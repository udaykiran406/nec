package com.nec.middleware.hr.dto.request;

import com.nec.middleware.template.annotation.ExcelColumn;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityTraineeRequestDto {

    @ExcelColumn(
            name = "University Trainee ID",
            sample = "UT001"
    )
    private String universityTraineeId;

    @ExcelColumn(
            name = "Full Name",
            mandatory = true,
            sample = "John Doe"
    )
    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must not exceed 120 characters")
    private String fullName;

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
            sample = "22",
            description = "Allowed range: 17–60"
    )
    @NotNull(message = "Age is required")
    @Min(value = 17, message = "Age must be at least 17")
    @Max(value = 60, message = "Age must not exceed 60")
    private Short age;

    @ExcelColumn(
            name = "Phone",
            mandatory = true,
            sample = "+91 9876543210",
            description = "Digits and + - ( ) only"
    )
    @NotBlank(message = "Phone number is required")
    @Size(min = 9, max = 30, message = "Phone must be between 9 and 30 characters")
    @Pattern(
            regexp = "^[0-9+\\-\\s()]+$",
            message = "Phone must contain only digits and allowed symbols"
    )
    private String phone;

    @ExcelColumn(
            name = "Email",
            mandatory = true,
            sample = "student@example.com"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @ExcelColumn(
            name = "Payment Method ID",
            mandatory = true,
            sample = "1",
            description = "Reference ID from payment method master"
    )
    @NotNull(message = "Payment method is required")
    private Long paymentMethodId;

    @ExcelColumn(
            name = "University ID",
            mandatory = true,
            sample = "10",
            description = "Reference ID from university master"
    )
    @NotNull(message = "University is required")
    private Long universityId;

    @ExcelColumn(
            name = "Semester",
            mandatory = true,
            sample = "Semester 6"
    )
    @NotBlank(message = "Semester is required")
    @Size(max = 50, message = "Semester must not exceed 50 characters")
    private String semester;

    @ExcelColumn(
            name = "Faculty",
            mandatory = true,
            sample = "Computer Science"
    )
    @NotBlank(message = "Faculty is required")
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
            sample = "12",
            description = "Reference ID from district master"
    )
    @NotNull(message = "District is required")
    private Long districtId;

    @ExcelColumn(
            name = "City ID",
            mandatory = true,
            sample = "120",
            description = "Reference ID from city master"
    )
    @NotNull(message = "City is required")
    private Long cityId;

    @ExcelColumn(
            name = "Photo Path",
            sample = "uploads/photo.jpg",
            description = "Generated automatically after upload"
    )
    private String photoPath;

//    @ExcelColumn(
//            name = "Status ID",
//            mandatory = true,
//            sample = "1",
//            description = "Reference ID from status master"
//    )
//    @NotNull(message = "Status is required")
//    private Long statusId;

    @ExcelColumn(
            name = "Created By",
            sample = "system",
            description = "Auto populated"
    )
    private String createdBy;

    @ExcelColumn(
            name = "Updated By",
            sample = "system",
            description = "Auto populated"
    )
    private String updatedBy;
}

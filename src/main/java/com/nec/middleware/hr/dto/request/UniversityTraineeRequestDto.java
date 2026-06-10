package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityTraineeRequestDto {

    // null → CREATE, non-null → UPDATE
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must not exceed 120 characters")
    private String fullName;

    @NotNull(message = "Gender is required")
    private Long genderId;

    @NotNull(message = "Age is required")
    @Min(value = 17, message = "Age must be at least 17")
    @Max(value = 60, message = "Age must not exceed 60")
    private Short age;

    @NotBlank(message = "Phone number is required")
    @Size(min = 9, max = 30, message = "Phone must be between 9 and 30 characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Phone must contain only digits and allowed symbols")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotNull(message = "Payment method is required")
    private Long paymentMethodId;

    @NotNull(message = "University is required")
    private Long universityId;

    @NotBlank(message = "Semester is required")
    @Size(max = 50, message = "Semester must not exceed 50 characters")
    private String semester;

    @NotBlank(message = "Faculty is required")
    @Size(max = 100, message = "Faculty must not exceed 100 characters")
    private String faculty;

    @NotNull(message = "Region is required")
    private Long regionId;

    @NotNull(message = "District is required")
    private Long districtId;

    @NotNull(message = "City is required")
    private Long cityId;

    @Size(max = 500, message = "Photo URL must not exceed 500 characters")
    private String photoUrl;

    @NotNull(message = "Status is required")
    private Long statusId;

    // Audit — populated from security context in the service layer
    private Long createdBy;
    private Long updatedBy;
}

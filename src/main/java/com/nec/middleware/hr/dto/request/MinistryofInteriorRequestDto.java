package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinistryofInteriorRequestDto {

    private String ministryofInteriorId;

    @NotNull(message = "Title type is required")
    private Long moiTitleId;

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must not exceed 120 characters")
    private String Name;

    @NotNull(message = "Gender is required")
    private Long genderId;

    @Min(value = 17, message = "Age must be at least 17")
    @Max(value = 100, message = "Age must not exceed 100")
    private Short age;

    @NotBlank(message = "Phone number is required")
    @Size(min = 9, max = 30, message = "Phone must be between 9 and 30 characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Phone must contain only digits and allowed symbols")
    private String phone;

    // Populated by the service layer after storing the uploaded multipart file.
    // Not settable by the client directly (no @Size/manual-entry validation needed).
    private String photoPath;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotNull(message = "Region is required")
    private Long regionId;

    @NotNull(message = "District is required")
    private Long districtId;

    @NotNull(message = "City is required")
    private Long cityId;

    @NotNull(message = "Voter Registration Center is required")
    private Long vrcId;

    @NotNull(message = "Status is required")
    private Long statusId;

    // Audit — populated from security context in the service layer
    private String createdBy;
    private String updatedBy;
}
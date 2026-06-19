package com.nec.middleware.hr.dto.request;

import com.nec.middleware.template.ExcelColumn;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoliticalPartyAgentRequestDto {

    // null → CREATE, non-null → UPDATE
    private String politicalPartyAgentUserId;

    @ExcelColumn(
            name = "Political Party ID",
            mandatory = true,
            sample = "1",
            description = "Reference ID from political party master"
    )
    @NotNull(message = "Political party is required")
    private Long politicalPartyNameId;

    @ExcelColumn(
            name = "Agent Name",
            mandatory = true,
            sample = "John Doe"
    )
    @NotBlank(message = "Agent name is required")
    @Size(max = 120, message = "Agent name must not exceed 120 characters")
    private String agentName;

    @ExcelColumn(
            name = "Gender ID",
            mandatory = true,
            sample = "1",
            description = "Reference ID from gender lookup"
    )
    @NotNull(message = "Gender is required")
    private Long genderId;

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
            name = "Email",
            mandatory = true,
            sample = "agent@example.com"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
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
            name = "Polling Station ID",
            mandatory = true,
            sample = "3",
            description = "Reference ID from polling station master"
    )
    @NotNull(message = "Polling station is required")
    private Long pollingStationId;

    @ExcelColumn(
            name = "Region ID",
            mandatory = true,
            sample = "2",
            description = "Reference ID from region master"
    )
    @NotNull(message = "Region is required")
    private Long regionId;

    @ExcelColumn(
            name = "District ID",
            mandatory = true,
            sample = "4",
            description = "Reference ID from district master"
    )
    @NotNull(message = "District is required")
    private Long districtId;

    @ExcelColumn(
            name = "City ID",
            mandatory = true,
            sample = "5",
            description = "Reference ID from city master"
    )
    @NotNull(message = "City is required")
    private Long cityId;

    // Audit — populated from security context in the service layer
    private String createdBy;
    private String updatedBy;
}

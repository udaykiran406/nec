package com.nec.middleware.hr.dto.request;

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

    @NotNull(message = "Political party is required")
    private Long politicalPartyNameId;

    @NotBlank(message = "Agent name is required")
    @Size(max = 120, message = "Agent name must not exceed 120 characters")
    private String agentName;

    @NotNull(message = "Gender is required")
    private Long genderId;

    @NotBlank(message = "Phone number is required")
    @Size(min = 9, max = 30, message = "Phone must be between 9 and 30 characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Phone must contain only digits and allowed symbols")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 500, message = "Photo URL must not exceed 500 characters")
    private String photoUrl;

    @NotNull(message = "Polling station is required")
    private Long pollingStationId;

    @NotNull(message = "Region is required")
    private Long regionId;

    @NotNull(message = "District is required")
    private Long districtId;

    @NotNull(message = "City is required")
    private Long cityId;

    @NotNull(message = "Status is required")
    private Long statusId;

    // Audit — populated from security context in the service layer
    private String createdBy;
    private String updatedBy;
}

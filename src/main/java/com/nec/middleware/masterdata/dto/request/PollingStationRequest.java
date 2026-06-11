package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollingStationRequest {

    private Long id;

    @NotBlank(message = "Polling station name must not be blank")
    @Size(max = 255, message = "Polling station name must not exceed 255 characters")
    private String pollingStationName;

    @NotBlank(message = "Polling station code must not be blank")
    @Size(max = 100, message = "Polling station code must not exceed 100 characters")
    private String pollingStationCode;

    @NotNull(message = "Voter capacity must not be null")
    @Min(value = 0, message = "Voter capacity must be 0 or greater")
    private Integer voterCapacity;

    @NotNull(message = "Region ID must not be null")
    @Min(value = 1, message = "Region ID must be a positive number")
    private Long regionId;

    @NotNull(message = "District ID must not be null")
    @Min(value = 1, message = "District ID must be a positive number")
    private Long districtId;

    @NotNull(message = "City ID must not be null")
    @Min(value = 1, message = "City ID must be a positive number")
    private Long cityId;

    @NotBlank(message = "Status must not be blank")
    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be 'Active' or 'Inactive'")
    private String status;

    @Min(value = 1, message = "createdBy must be a positive number")
    private Long createdBy;

    @Min(value = 1, message = "updatedBy must be a positive number")
    private Long updatedBy;
}
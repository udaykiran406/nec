package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityRequest {

    private Long id;

    @NotNull(message = "Region ID must not be null")
    @Min(value = 1, message = "Region ID must be a positive number")
    private Long regionId;

    @NotNull(message = "District ID must not be null")
    @Min(value = 1, message = "District ID must be a positive number")
    private Long districtId;

    @NotBlank(message = "City name must not be blank")
    @Size(min = 1, max = 150, message = "City name must be between 1 and 150 characters")
    private String cityName;

    @NotBlank(message = "Status must not be blank")
    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be 'Active' or 'Inactive'")
    private String status;

    @Min(value = 1, message = "createdBy must be a positive number")
    private Long createdBy;

    @Min(value = 1, message = "updatedBy must be a positive number")
    private Long updatedBy;
}
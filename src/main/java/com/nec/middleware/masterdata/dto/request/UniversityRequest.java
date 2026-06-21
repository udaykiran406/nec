package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityRequest {

    private Long id;

    @NotBlank(message = "University name must not be blank")
    @Size(max = 200, message = "University name must not exceed 200 characters")
    private String universityName;

    @NotNull(message = "Region ID must not be null")
    @Min(value = 1, message = "Region ID must be a positive number")
    private Long regionId;

    @NotNull(message = "District ID must not be null")
    @Min(value = 1, message = "District ID must be a positive number")
    private Long districtId;

    @NotNull(message = "City ID must not be null")
    @Min(value = 1, message = "City ID must be a positive number")
    private Long cityId;

    @NotBlank(message = "Location must not be blank")
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @NotBlank(message = "Status must not be blank")
    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be 'Active' or 'Inactive'")
    private String status;

    private String createdBy;

    private String updatedBy;
}
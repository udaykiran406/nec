package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseRequest {

    private Long id;

    @NotBlank(message = "Warehouse name must not be blank")
    @Size(max = 200)
    private String warehouseName;

    @NotNull
    private Long regionId;

    @NotNull
    private Long districtId;

    @NotNull
    private Long cityId;

    @NotBlank
    @Size(max = 200)
    private String purpose;

    @NotBlank
    @Pattern(regexp = "^(Active|Inactive)$")
    private String status;

    private Long createdBy;

    private Long updatedBy;
}
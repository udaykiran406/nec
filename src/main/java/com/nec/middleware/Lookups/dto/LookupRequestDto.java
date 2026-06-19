package com.nec.middleware.Lookups.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookupRequestDto {

    private Long id; // null = Add, not null = Edit

    @NotBlank(message = "Table name is required")
    private String tableName;

    @NotBlank(message = "Lookup value is required")
    private String value;

    private String description;

    @NotNull(message = "Status is required")
    private Integer isActive;

    private Integer displayOrder;
}
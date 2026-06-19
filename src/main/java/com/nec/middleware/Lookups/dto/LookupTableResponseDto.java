package com.nec.middleware.Lookups.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookupTableResponseDto {

    private Long id;

    private String tableName;

    private String displayName;

    private Integer isActive;
}
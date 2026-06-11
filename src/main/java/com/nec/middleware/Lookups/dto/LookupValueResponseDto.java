package com.nec.middleware.Lookups.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookupValueResponseDto {

    private Long id;



    private String code;

    private String value;

    private String description;

    private Integer isActive;

    private Integer displayOrder;
}
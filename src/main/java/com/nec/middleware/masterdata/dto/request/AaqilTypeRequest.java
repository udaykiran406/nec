package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AaqilTypeRequest {

    private Long id;

    @NotBlank
    @Size(max = 30)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String value;

    @Size(max = 255)
    private String description;

    private Boolean isActive;

    private String createdBy;

    private String updatedBy;
}
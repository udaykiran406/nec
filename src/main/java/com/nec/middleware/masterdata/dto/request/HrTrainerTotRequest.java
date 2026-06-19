package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrTrainerTotRequest {

    private Long id;

    @NotBlank
    @Size(max = 20)
    private String code;

    @NotBlank
    @Size(max = 150)
    private String fullName;

    @Size(max = 30)
    private String phone;

    @Email
    @Size(max = 180)
    private String email;

    @Size(max = 255)
    private String description;

    private Boolean isActive;

    private String createdBy;

    private String updatedBy;
}
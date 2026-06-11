package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeploymentRoleRequest {

    private Long id;

    @NotBlank(message = "Role name must not be blank")
    @Size(max = 200)
    private String roleName;

    @NotBlank(message = "Status must not be blank")
    @Pattern(regexp = "^(Active|Inactive)$")
    private String status;

    private Long createdBy;

    private Long updatedBy;
}
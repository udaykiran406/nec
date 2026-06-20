package com.nec.middleware.rbacAuth.rbac.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;
import java.time.LocalDate;
/**
 * Request DTO for paginated, searchable, and filterable module list API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleListRequestDto {
    @Min(value = 0, message = "Page must be zero or greater.")
    private Integer page;

    @Min(value = 1, message = "Size must be at least 1.")
    private Integer size;

    private String sortBy;

    private String sortDirection;

    private String search;

    private Long moduleId;

    private String moduleCode;

    private String moduleName;

    private String status;

    private LocalDate fromDate;

    private LocalDate toDate;
}

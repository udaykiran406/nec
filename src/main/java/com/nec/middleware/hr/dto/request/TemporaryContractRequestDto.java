package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporaryContractRequestDto {

    // null -> CREATE, non-null -> UPDATE
    private String contractNo;

    @NotBlank(message = "Employer name is required")
    @Size(max = 200, message = "Employer name must not exceed 200 characters")
    private String employerName;

    @NotBlank(message = "Employee name is required")
    @Size(max = 150, message = "Employee name must not exceed 150 characters")
    private String employeeName;

    @NotNull(message = "Contract type is required")
    private Long contractTypeId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Total contract amount is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalContractAmount;

    @NotNull(message = "Initial payment percentage is required")
    private BigDecimal initialPaymentPercentage;

    @NotNull(message = "Remaining percentage is required")
    private BigDecimal remainingPercentage;

    @Size(max = 2000)
    private String jobDescription;

    @Size(max = 5000)
    private String termsAndConditions;
    @NotNull(message = "Status is required")
    private Long statusId;
    private String createdBy;
    private String updatedBy;
}

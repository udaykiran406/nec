package com.nec.middleware.hr.dto.request;

import com.nec.middleware.template.ExcelColumn;
import jakarta.validation.constraints.*;
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
    private String contractId;
    @ExcelColumn(
            name = "Employer Name",
            mandatory = true,
            sample = "NEC Procurement Office"
    )
    @NotBlank(message = "Employer name is required")
    @Size(max = 200, message = "Employer name must not exceed 200 characters")
    private String employerName;
    @ExcelColumn(
            name = "Employee Name",
            mandatory = true,
            sample = "Ahmed Ibrahim"
    )
    @NotBlank(message = "Employee name is required")
    @Size(max = 150, message = "Employee name must not exceed 150 characters")
    private String employeeName;

    @ExcelColumn(
            name = "Contact No",
            mandatory = true,
            sample = "+252612345678",
            description = "Digits and + - ( ) spaces only. 9–20 characters"
    )
    @NotBlank(message = "Contact number is required")
    @Size(min = 9, max = 20, message = "Contact must be between 9 and 20 characters")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Contact must contain only digits and allowed symbols")
    private String contactNo;

    @ExcelColumn(
            name = "Contract Type ID",
            mandatory = true,
            sample = "1",
            description = "Reference ID from contract type master table"
    )
    @NotNull(message = "Contract type is required")
    private Long contractTypeId;

    @ExcelColumn(
            name = "Start Date",
            mandatory = true,
            sample = "2025-01-15",
            description = "Format: YYYY-MM-DD"
    )
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @ExcelColumn(
            name = "End Date",
            mandatory = true,
            sample = "2025-12-31",
            description = "Format: YYYY-MM-DD"
    )
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @ExcelColumn(
            name = "Total Contract Amount",
            mandatory = true,
            sample = "15000.00",
            description = "Numeric value greater than 0"
    )
    @NotNull(message = "Total contract amount is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalContractAmount;

    @ExcelColumn(
            name = "Initial Payment Percentage",
            mandatory = true,
            sample = "30.00",
            description = "Numeric percentage e.g. 30.00"
    )
    @NotNull(message = "Initial payment percentage is required")
    private BigDecimal initialPaymentPercentage;

    @ExcelColumn(
            name = "Remaining Percentage",
            mandatory = true,
            sample = "70.00",
            description = "Numeric percentage e.g. 70.00"
    )
    @NotNull(message = "Remaining percentage is required")
    private BigDecimal remainingPercentage;

    @ExcelColumn(
            name = "Job Description",
            sample = "Provide electoral support services across all regions.",
            description = "Optional. Max 2000 characters"
    )
    @Size(max = 2000)
    private String jobDescription;
    @ExcelColumn(
            name = "Terms and Conditions",
            sample = "Payment subject to milestone completion.",
            description = "Optional. Max 5000 characters"
    )
    @Size(max = 5000)
    private String termsAndConditions;

    @NotNull(message = "Status is required")
    private String status;
    private String createdBy;
    private String updatedBy;
}

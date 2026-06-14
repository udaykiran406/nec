package com.nec.middleware.hr.dto.response;

import com.nec.middleware.dto.IdValueDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporaryContractResponseDto {

    private String contractNo;
    private String contractId;
    private String employerName;

    private String employeeName;

    private IdValueDto contractType;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal totalContractAmount;

    private BigDecimal initialPaymentPercentage;

    private BigDecimal remainingPercentage;

    private String jobDescription;

    private String termsAndConditions;

    private IdValueDto status;
    private String currentApproval;

    private Boolean isActive;

    private String createdBy;

    private String updatedBy;
}
package com.nec.middleware.hr.dto.response;

import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.workflow.dto.response.ApprovalLevelStatusDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporaryContractResponseDto {

    private String taskId;
    private String taskName;

    private String contractId;
    private String employerName;

    private String employeeName;
    private String contactNo;
    private IdValueDto contractType;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal totalContractAmount;

    private BigDecimal initialPaymentPercentage;

    private BigDecimal remainingPercentage;

    private String jobDescription;

    private String termsAndConditions;

    private IdValueDto status;

    private Boolean isActive;

    private String createdBy;

    private String updatedBy;
    private List<ApprovalLevelStatusDto> approvalHistory;

}
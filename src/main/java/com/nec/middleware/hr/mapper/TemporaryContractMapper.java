package com.nec.middleware.hr.mapper;

import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.hr.dto.response.TemporaryContractResponseDto;
import com.nec.middleware.hr.entity.TemporaryContract;
import org.springframework.stereotype.Component;

@Component
public class TemporaryContractMapper {

    public TemporaryContract temporaryContractEntity(
            TemporaryContractRequestDto temporaryContractRequestDto) {

        TemporaryContract temporaryContract = TemporaryContract.builder()
                .startDate(temporaryContractRequestDto.getStartDate())
                .endDate(temporaryContractRequestDto.getEndDate())
                .contactNo(temporaryContractRequestDto.getContactNo())
                .totalContractAmount(temporaryContractRequestDto.getTotalContractAmount())
                .initialPaymentPercentage(temporaryContractRequestDto.getInitialPaymentPercentage())
                .remainingPercentage(temporaryContractRequestDto.getRemainingPercentage())
                .jobDescription(temporaryContractRequestDto.getJobDescription())
                .termsAndConditions(temporaryContractRequestDto.getTermsAndConditions())
                .build();

        temporaryContract.setIsActive(Boolean.TRUE);
        temporaryContract.setCreatedBy(temporaryContractRequestDto.getCreatedBy());
        temporaryContract.setUpdatedBy(temporaryContractRequestDto.getCreatedBy());

        return temporaryContract;
    }

    public TemporaryContractResponseDto temporaryContractResponseDto(
            TemporaryContract entity) {

        return TemporaryContractResponseDto.builder()

                // Identity
                .contractId(entity.getContractId())
                .employerName(entity.getEmployerName())
                .employeeName(entity.getEmployeeName())
                .contactNo(entity.getContactNo())

                // Contract Type
                .contractType(entity.getContractType() != null
                        ? IdValueDto.builder()
                        .id(entity.getContractType().getId())
                        .value(entity.getContractType().getValue())
                        .build()
                        : null)

                // Contract Details
//                .startDate(entity.getStartDate())
//                .endDate(entity.getEndDate())

                .totalContractAmount(entity.getTotalContractAmount())

                .initialPaymentPercentage(
                        entity.getInitialPaymentPercentage())

                .remainingPercentage(
                        entity.getRemainingPercentage())

//                .initialPaymentAmount(
//                        entity.getInitialPaymentAmount())
//
//                .remainingPaymentAmount(
//                        entity.getRemainingPaymentAmount())

                .jobDescription(
                        entity.getJobDescription())

                .termsAndConditions(
                        entity.getTermsAndConditions())

                // Status
                .status(entity.getStatus() != null
                        ? IdValueDto.builder()
                        .id(entity.getStatus().getId())
                        .value(entity.getStatus().getValue())
                        .build()
                        : null)

                // Workflow
//                .processInstanceId(
//                        entity.getProcessInstanceId())

//                .currentApproval(
//                        entity.getCurrentApproval())
//
//                .approvalRemarks(
//                        entity.getApprovalRemarks())

                // Audit
                .isActive(entity.getIsActive())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}

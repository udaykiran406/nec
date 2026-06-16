package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.ContractTypeRepository;
import com.nec.middleware.Lookups.repository.TemporaryContractStatusRepository;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.hr.entity.TemporaryContract;
import com.nec.middleware.hr.mapper.TemporaryContractMapper;
import com.nec.middleware.hr.repository.TemporaryContractRepository;
import com.nec.middleware.hr.service.TemporaryContractService;
import com.nec.middleware.workflow.dto.response.WorkflowSubmissionResponseDto;
import com.nec.middleware.workflow.mapper.WorkflowSubmissionResponseDtoMapper;
import com.nec.middleware.workflow.service.WorkflowService;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import com.nec.middleware.masterdata.repository.ApprovalWorkflowLevelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;

import static com.nec.middleware.idGenerator.Enum.ModuleCode.TEMPORARY_CONTRACT;

@Service
@RequiredArgsConstructor
public class TemporaryContractServiceImpl implements TemporaryContractService {

    TemporaryContractMapper temporaryContractMapper;
    TemporaryContractRepository temporaryContractRepository;

    UniqueIdGeneratorService uniqueIdGeneratorService;
    ContractTypeRepository contractTypeRepository;

    TemporaryContractStatusRepository temporaryContractStatusRepository;

    ApprovalWorkflowLevelRepository approvalWorkflowLevelRepository;
    WorkflowService workflowService;

    WorkflowSubmissionResponseDtoMapper workflowSubmissionResponseDtoMapper;

    private static final String WORKFLOW_MODULE_NAME="TEMPORARY_CONTRACT";
    @Override
    @Transactional
    public WorkflowSubmissionResponseDto createTemporaryContract(TemporaryContractRequestDto temporaryContractRequestDto) {

        TemporaryContract temporaryContract =
                temporaryContractMapper.temporaryContractEntity(temporaryContractRequestDto);

        temporaryContract.setContractType(
                contractTypeRepository.findById(temporaryContractRequestDto.getContractTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Contract Type not found")));

        temporaryContract.setStatus(
                temporaryContractStatusRepository.findById(temporaryContractRequestDto.getStatusId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Status not found")));

        calculatePaymentAmounts(temporaryContract);

        TemporaryContract savedContract = temporaryContractRepository.save(temporaryContract);

        if(temporaryContractRequestDto.getStatusId()!=1) {
            String contractId = generateContractNumber();
            temporaryContract.setContractId(contractId);
            ApprovalWorkflowLevel firstLevelApprover =
                    approvalWorkflowLevelRepository
                            .findByModuleNameAndLevelOrder(
                                    WORKFLOW_MODULE_NAME,
                                    1)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Approval level not found"));
            String processInstanceId =
                    workflowService.startApprovalWorkflow(firstLevelApprover,
                            contractId,"TEMPORARY_CONTRACT",savedContract.getCreatedBy());
            savedContract.setProcessInstanceId(processInstanceId);
            savedContract = temporaryContractRepository.save(savedContract);
            workflowService.createWorkflowAuditRecords(savedContract.getContractId(),WORKFLOW_MODULE_NAME,processInstanceId);
        }

//        TemporaryContractResponseDto temporaryContractResponseDto = temporaryContractMapper.temporaryContractResponseDto(temporaryContract);
//
//        temporaryContractResponseDto.setApprovalHistory(workflowService.buildApprovalHistory(WORKFLOW_MODULE_NAME,savedContract.getContractId()));
        return workflowSubmissionResponseDtoMapper.workflowSubmissionResponseDto(savedContract,WORKFLOW_MODULE_NAME);
    }

    // Helper Methods

    private void calculatePaymentAmounts(
            TemporaryContract contract) {

        BigDecimal totalAmount =
                contract.getTotalContractAmount();

        BigDecimal initialAmount =
                totalAmount.multiply(
                                contract.getInitialPaymentPercentage())
                        .divide(BigDecimal.valueOf(100));

        BigDecimal remainingAmount =
                totalAmount.multiply(
                                contract.getRemainingPercentage())
                        .divide(BigDecimal.valueOf(100));

        contract.setInitialPaymentAmount(initialAmount);
        contract.setRemainingPaymentAmount(remainingAmount);
    }

    public String generateContractNumber() {

        String prefix = "TC-" + Year.now().getValue() + "-";

        return uniqueIdGeneratorService.generateId(
                        TEMPORARY_CONTRACT,
                        prefix);
    }
}

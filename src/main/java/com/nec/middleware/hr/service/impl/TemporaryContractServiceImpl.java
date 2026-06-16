package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.ContractTypeRepository;
import com.nec.middleware.Lookups.repository.TemporaryContractStatusRepository;
import com.nec.middleware.constants.Constants;
import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.hr.entity.TemporaryContract;
import com.nec.middleware.hr.mapper.TemporaryContractMapper;
import com.nec.middleware.hr.repository.TemporaryContractRepository;
import com.nec.middleware.hr.service.TemporaryContractService;
import com.nec.middleware.workflow.dto.response.WorkflowInboxDto;
import com.nec.middleware.workflow.mapper.WorkflowInboxMapper;
import com.nec.middleware.workflow.service.WorkflowService;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import com.nec.middleware.masterdata.repository.ApprovalWorkflowLevelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;

import static com.nec.middleware.idGenerator.Enum.ModuleCode.TEMPORARY_CONTRACT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemporaryContractServiceImpl implements TemporaryContractService {

    private final TemporaryContractMapper temporaryContractMapper;
    private final TemporaryContractRepository temporaryContractRepository;

    private final UniqueIdGeneratorService uniqueIdGeneratorService;
    private final ContractTypeRepository contractTypeRepository;

    private final TemporaryContractStatusRepository temporaryContractStatusRepository;

    private final ApprovalWorkflowLevelRepository approvalWorkflowLevelRepository;
    private final WorkflowService workflowService;

    private final WorkflowInboxMapper workflowSubmissionResponseDtoMapper;


    private static final String DRAFT="DRAFT";
    @Override
    @Transactional
    public WorkflowInboxDto createTemporaryContract(TemporaryContractRequestDto temporaryContractRequestDto) {

        log.info("Creating temporary contract --->");
        TemporaryContract temporaryContract =
                temporaryContractMapper.temporaryContractEntity(temporaryContractRequestDto);

        temporaryContract.setContractType(
                contractTypeRepository.findById(temporaryContractRequestDto.getContractTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Contract Type not found")));
//        temporaryContract.setStatus(
//                temporaryContractStatusRepository.findById(temporaryContractRequestDto.getStatusId())
//                        .orElseThrow(() ->
//                                new ResourceNotFoundException("Status not found")));
        temporaryContract.setStatus(temporaryContractRequestDto.getStatus());
        calculatePaymentAmounts(temporaryContract);
        String contractId = generateContractNumber();
        temporaryContract.setContractId(contractId);
        log.info("contractId generated successfully {}",contractId);
        TemporaryContract savedContract = temporaryContractRepository.save(temporaryContract);
        log.info("created by after saving {} ",savedContract.getCreatedBy());
        if (savedContract.getStatus().equalsIgnoreCase(DRAFT)) {
            return saveAsDraft(savedContract);
        }
        savedContract.setStatus(Constants.WORKFLOW_PENDING_STATUS);
        return submitForApproval(savedContract);

    }

    private WorkflowInboxDto saveAsDraft(TemporaryContract contract) {

        return workflowSubmissionResponseDtoMapper
                .workflowSubmissionResponseDto(
                        contract.getContractId(),
                        Constants.TEMPORARY_CONTRACT_WORKFLOW_MODULE_NAME,
                        null,
                        null,
                        contract.getStatus());
    }

    private WorkflowInboxDto submitForApproval(TemporaryContract contract) {

        ApprovalWorkflowLevel firstLevel =
                approvalWorkflowLevelRepository
                        .findByModuleNameAndLevelOrder(
                                Constants.TEMPORARY_CONTRACT_WORKFLOW_MODULE_NAME,
                                1)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Approval level not found"));

        contract.setCurrentApproval(firstLevel.getApprovalRole());

        String processInstanceId =
                workflowService.startApprovalWorkflow(
                        firstLevel,
                        contract.getContractId(),
                        Constants.TEMPORARY_CONTRACT_WORKFLOW_MODULE_NAME,
                        contract.getCreatedBy());

        contract.setProcessInstanceId(
                processInstanceId);
        log.info("Process instance Id generated Successfully {}",processInstanceId);
        contract =
                temporaryContractRepository.save(
                        contract);

        workflowService.createWorkflowAuditRecords(
                contract.getContractId(),
                Constants.TEMPORARY_CONTRACT_WORKFLOW_MODULE_NAME,
                processInstanceId,contract.getCreatedBy());

        WorkflowInboxDto response =
                workflowSubmissionResponseDtoMapper
                        .workflowSubmissionResponseDto(
                                contract.getContractId(),
                                Constants.TEMPORARY_CONTRACT_WORKFLOW_MODULE_NAME,
                                contract.getCurrentApproval(),
                                contract.getProcessInstanceId(),
                                contract.getStatus());

//        response.setApprovalHistory(
//                workflowService.buildApprovalHistory(
//                        WORKFLOW_MODULE_NAME,
//                        contract.getContractId()));

        return response;
    }

    private void calculatePaymentAmounts(TemporaryContract contract) {

        log.info("calculation logic for given total amount");
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

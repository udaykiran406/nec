package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.ContractTypeRepository;
import com.nec.middleware.Lookups.repository.TemporaryContractStatusRepository;
import com.nec.middleware.constants.Constants;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.hr.dto.response.TemporaryContractResponseDto;
import com.nec.middleware.hr.entity.TemporaryContract;
import com.nec.middleware.hr.mapper.TemporaryContractMapStruct;
import com.nec.middleware.hr.repository.TemporaryContractRepository;
import com.nec.middleware.hr.service.TemporaryContractService;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import com.nec.middleware.masterdata.repository.ApprovalWorkflowLevelRepository;
import com.nec.middleware.workflow.mapper.WorkflowInboxMapper;
import com.nec.middleware.workflow.service.WorkflowService;
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

    private final TemporaryContractMapStruct temporaryContractMapStruct;

    private final TemporaryContractRepository temporaryContractRepository;

    private final UniqueIdGeneratorService uniqueIdGeneratorService;
    private final ContractTypeRepository contractTypeRepository;

    private final TemporaryContractStatusRepository temporaryContractStatusRepository;

    private final ApprovalWorkflowLevelRepository approvalWorkflowLevelRepository;
    private final WorkflowService workflowService;

    private final WorkflowInboxMapper workflowSubmissionResponseDtoMapper;
    private static final String DRAFT = "DRAFT";
    private static final String REQUESTER_ROLE = "HR_OFFICER";

    // for testing requester role and name is hardcoded at line 138
    @Override
    @Transactional
    public TemporaryContractResponseDto createOrUpdateContract(TemporaryContractRequestDto temporaryContractRequestDto) {

        TemporaryContract temporaryContract;
        // UPDATE FLOW
        if (temporaryContractRequestDto.getContractId() != null
                && !temporaryContractRequestDto.getContractId().isBlank()) {

            log.info("Updating existing contract: {}",
                    temporaryContractRequestDto.getContractId());

            temporaryContract = temporaryContractRepository
                    .findByContractId(temporaryContractRequestDto.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contract not found with id: "
                                    + temporaryContractRequestDto.getContractId()));

            // Update fields from request
            temporaryContractMapStruct.updateEntityFromDto(
                    temporaryContractRequestDto, temporaryContract);

        } else {

            // CREATE FLOW
            log.info("Creating new contract");

            temporaryContract =
                    temporaryContractMapStruct.temporaryContractEntity(
                            temporaryContractRequestDto);
            temporaryContract.setCreatedBy(temporaryContractRequestDto.getCreatedBy());
            temporaryContract.setUpdatedBy(temporaryContractRequestDto.getUpdatedBy());
            String contractId = generateContractNumber();
            temporaryContract.setContractId(contractId);
            temporaryContract.setIsActive(true);
            log.info("ContractId generated successfully {}", contractId);
        }

        temporaryContract.setContractType(
                contractTypeRepository.findById(
                                temporaryContractRequestDto.getContractTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contract Type not found")));

        temporaryContract.setStatus(
                temporaryContractRequestDto.getStatus());

        calculatePaymentAmounts(temporaryContract);

        TemporaryContract savedContract =
                temporaryContractRepository.save(temporaryContract);

        log.info("Created/Updated successfully. CreatedBy: {}",
                savedContract.getCreatedBy());

        // DRAFT -> only save
        if (DRAFT.equalsIgnoreCase(savedContract.getStatus())) {
            return saveAsDraft(savedContract);
        }

        // SUBMITTED -> start workflow
        savedContract.setStatus(Constants.WORKFLOW_PENDING_STATUS);
        return submitForApproval(savedContract);
    }

    private TemporaryContractResponseDto saveAsDraft(TemporaryContract savedContract) {
        return temporaryContractMapStruct.temporaryContractResponseDto(savedContract);
    }

    private TemporaryContractResponseDto submitForApproval(TemporaryContract contract) {

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
                        contract.getCreatedBy(), REQUESTER_ROLE);

        contract.setProcessInstanceId(processInstanceId);
        log.info("Process instance Id generated Successfully {}", processInstanceId);
        contract.setRevisionNo(1);
        contract = temporaryContractRepository.save(contract);

        workflowService.createWorkflowAuditRecords(
                contract.getContractId(),
                Constants.TEMPORARY_CONTRACT_WORKFLOW_MODULE_NAME,
                processInstanceId, contract.getCreatedBy(), REQUESTER_ROLE);

//        WorkflowInboxDto response =
//                workflowSubmissionResponseDtoMapper
//                        .workflowSubmissionResponseDto(
//                                contract.getContractId(),
//                                Constants.TEMPORARY_CONTRACT_WORKFLOW_MODULE_NAME,
//                                contract.getCurrentApproval(),
//                                contract.getProcessInstanceId(),
//                                contract.getStatus());

//        response.setApprovalHistory(
//                workflowService.buildApprovalHistory(
//                        WORKFLOW_MODULE_NAME,
//                        contract.getContractId()));

        return temporaryContractMapStruct.temporaryContractResponseDto(contract);
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

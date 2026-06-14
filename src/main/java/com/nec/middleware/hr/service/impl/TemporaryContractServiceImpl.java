package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.ContractTypeRepository;
import com.nec.middleware.Lookups.repository.TemporaryContractStatusRepository;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.hr.dto.response.TemporaryContractResponseDto;
import com.nec.middleware.hr.entity.TemporaryContract;
import com.nec.middleware.hr.mapper.TemporaryContractMapper;
import com.nec.middleware.hr.repository.TemporaryContractRepository;
import com.nec.middleware.hr.service.TemporaryContractService;
import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemporaryContractServiceImpl implements TemporaryContractService {

    TemporaryContractMapper temporaryContractMapper;
    TemporaryContractRepository temporaryContractRepository;

    UniqueIdGeneratorService uniqueIdGeneratorService;
    ContractTypeRepository contractTypeRepository;

    TemporaryContractStatusRepository temporaryContractStatusRepository;
    @Override
    @Transactional
    public TemporaryContractResponseDto createTemporaryContract(
            TemporaryContractRequestDto temporaryContractRequestDto) {

        TemporaryContract temporaryContract =
                temporaryContractMapper.temporaryContractEntity(temporaryContractRequestDto);

        temporaryContract.setContractId(generateContractNumber());

        temporaryContract.setContractType(
                contractTypeRepository.findById(temporaryContractRequestDto.getContractTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Contract Type not found")));

        temporaryContract.setStatus(
                temporaryContractStatusRepository.findById(temporaryContractRequestDto.getStatusId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Status not found")));

        calculatePaymentAmounts(temporaryContract);

        temporaryContractRepository.save(temporaryContract);

        return temporaryContractMapper
                .temporaryContractResponseDto(temporaryContract);
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
                        ModuleCode.TEMPORARY_CONTRACT,
                        prefix);
    }
}

package com.nec.middleware.Lookups.service;

import com.nec.middleware.Lookups.dto.LookupRequestDto;
import com.nec.middleware.Lookups.dto.LookupTableResponseDto;
import com.nec.middleware.Lookups.dto.LookupValueResponseDto;
import com.nec.middleware.Lookups.entity.LookupEntity;
import com.nec.middleware.Lookups.repository.LookupDataRepository;
import com.nec.middleware.Lookups.repository.LookupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LookupServiceImpl implements LookupService {

    private static final Integer ACTIVE = 1;

    private final LookupRepository lookupRepository;
    private final LookupDataRepository lookupDataRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LookupTableResponseDto> getAllActiveLookupTables() {
        return lookupRepository.findByIsActiveOrderByTableNameAsc(ACTIVE)
                .stream()
                .map(entity -> LookupTableResponseDto.builder()
                        .id(entity.getId())
                        .tableName(entity.getTableName())
                        .displayName(entity.getDisplayName())
                        .isActive(entity.getIsActive())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LookupValueResponseDto> getLookupValuesByTableName(String tableName) {
        String cleanedTableName = validateAndGetActiveTableName(tableName);
        return lookupDataRepository.getLookupValuesByTableName(cleanedTableName);
    }

    @Override
    public LookupValueResponseDto addOrEditLookupValue(LookupRequestDto requestDto) {
        validateLookupRequest(requestDto);

        String tableName = validateAndGetActiveTableName(requestDto.getTableName());

        if (requestDto.getId() == null) {
            return createLookupValue(tableName, requestDto);
        }

        return updateLookupValue(tableName, requestDto);
    }

    private LookupValueResponseDto createLookupValue(
            String tableName,
            LookupRequestDto requestDto) {

        String value = requestDto.getValue().trim();

        if (lookupDataRepository.existsByValue(tableName, value)) {
            throw new IllegalArgumentException("Lookup value already exists in table: " + tableName);
        }

        Integer displayOrder = requestDto.getDisplayOrder() != null
                ? requestDto.getDisplayOrder()
                : lookupDataRepository.getNextDisplayOrder(tableName);

        String generatedCode = generateLookupCode();

        Long insertedId = lookupDataRepository.insertLookupValue(
                tableName,
                generatedCode,
                value,
                trimToNull(requestDto.getDescription()),
                requestDto.getIsActive(),
                displayOrder
        );

        return LookupValueResponseDto.builder()
                .id(insertedId)

                .code(generatedCode)
                .value(value)
                .description(trimToNull(requestDto.getDescription()))
                .isActive(requestDto.getIsActive())
                .displayOrder(displayOrder)
                .build();
    }

    private LookupValueResponseDto updateLookupValue(
            String tableName,
            LookupRequestDto requestDto) {

        Long id = requestDto.getId();

        if (!lookupDataRepository.existsById(tableName, id)) {
            throw new IllegalArgumentException("Lookup record not found with id: " + id);
        }

        String value = requestDto.getValue().trim();

        if (lookupDataRepository.existsByValueExcludingId(tableName, value, id)) {
            throw new IllegalArgumentException("Lookup value already exists in table: " + tableName);
        }

        LookupValueResponseDto existingRecord =
                lookupDataRepository.getLookupValueById(tableName, id);

        Integer displayOrder = requestDto.getDisplayOrder() != null
                ? requestDto.getDisplayOrder()
                : existingRecord.getDisplayOrder();

        Integer isActive = requestDto.getIsActive() != null
                ? requestDto.getIsActive()
                : existingRecord.getIsActive();

        lookupDataRepository.updateLookupValue(
                tableName,
                id,
                value,
                trimToNull(requestDto.getDescription()),
                isActive,
                displayOrder
        );

        return lookupDataRepository.getLookupValueById(tableName, id);
    }

    private String validateAndGetActiveTableName(String tableName) {
        validateTableName(tableName);

        String cleanedTableName = tableName.trim();

        LookupEntity lookupEntity = lookupRepository
                .findByTableNameAndIsActive(cleanedTableName, ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lookup table is not configured or inactive: " + cleanedTableName
                ));

        return lookupEntity.getTableName();
    }

    private void validateLookupRequest(LookupRequestDto requestDto) {
        if (requestDto == null) {
            throw new IllegalArgumentException("Lookup request cannot be null");
        }

        validateTableName(requestDto.getTableName());

        if (requestDto.getValue() == null || requestDto.getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Lookup value is required");
        }

        if (requestDto.getIsActive() == null) {
            throw new IllegalArgumentException("isActive is required");
        }

        if (requestDto.getIsActive() != 0 && requestDto.getIsActive() != 1) {
            throw new IllegalArgumentException("isActive must be 0 or 1");
        }

        if (requestDto.getId() != null && requestDto.getId() <= 0) {
            throw new IllegalArgumentException("Valid id is required for edit");
        }
    }

    private void validateTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name is required");
        }

        if (!tableName.matches("^nec_lkp_[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid lookup table name");
        }
    }

    private String  generateLookupCode() {

        Long nextNumber = lookupDataRepository.getNextGlobalCodeNumber();

        return "LKP" + String.format("%03d", nextNumber);
    }



    private String trimToNull(String value) {
        return value == null || value.trim().isEmpty()
                ? null
                : value.trim();
    }
}
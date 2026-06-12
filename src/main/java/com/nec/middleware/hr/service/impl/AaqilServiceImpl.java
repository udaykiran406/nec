package com.nec.middleware.hr.service.impl;

import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.hr.constant.AaqilConstants;
import com.nec.middleware.hr.dto.request.AaqilListRequestDto;
import com.nec.middleware.hr.dto.request.AaqilRequestDto;
import com.nec.middleware.hr.dto.response.AaqilResponseDto;
import com.nec.middleware.hr.entity.Aaqil;
import com.nec.middleware.hr.mapper.AaqilMapper;
import com.nec.middleware.hr.repository.AaqilRepository;
import com.nec.middleware.hr.service.AaqilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AaqilServiceImpl implements AaqilService {

    private static final String CODE_PREFIX = "AA";
    private static final int    CODE_PAD    = 3;   // AA001, AA002 …

    private final AaqilRepository repository;
    private final AaqilMapper      mapper;

    // ------------------------------------------------------------------ SAVE / UPDATE

    @Override
    @Transactional
    public AaqilResponseDto saveOrUpdate(AaqilRequestDto requestDto) {

        validateAaqil(requestDto);
        Aaqil entity;

        if (requestDto.getId() == null) {

            log.info("Creating aaqil");
            entity = mapper.toEntity(requestDto);
            entity.setCode(generateCode());

        } else {

            log.info("Updating aaqil id: {}", requestDto.getId());
            entity = findByIdAndNotDeleted(requestDto.getId());
            mapper.updateEntity(entity, requestDto);
            // code is never changed on update
        }

        Aaqil saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public AaqilResponseDto getById(Long id) {
        return mapper.toResponseDto(findByIdAndNotDeleted(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AaqilResponseDto> getAll(AaqilListRequestDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository.findAllWithFilters(
                filterDto.getAaqilTypeId(),
                filterDto.getRegionId(),
                filterDto.getDistrictId(),
                filterDto.getCityId(),
                filterDto.getStatusId(),
                filterDto.getIsActive(),
                pageable
        ).map(mapper::toResponseDto);
    }

    // ------------------------------------------------------------------ STATUS CHANGE

    @Override
    @Transactional
    public AaqilResponseDto changeStatus(Long id) {

        Aaqil entity = findById(id);
        boolean newStatus = !entity.getIsActive();
        entity.setIsActive(newStatus);

        log.info("Changing status for aaqil id: {} → isActive={}", id, newStatus);

        return mapper.toResponseDto(repository.save(entity));
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public void softDelete(Long id) {

        Aaqil entity = findById(id);
        entity.setIsActive(false);

        repository.save(entity);
        log.info("Aaqil soft deleted with id: {}", id);
    }

    // ------------------------------------------------------------------
    // Code generation
    // ------------------------------------------------------------------

    private String generateCode() {
        int next = repository.findMaxCodeSequence() + 1;
        return CODE_PREFIX + String.format("%0" + CODE_PAD + "d", next);
    }

    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    private void validateAaqil(AaqilRequestDto dto) {

        if (dto.getId() == null) {

            if (repository.existsByEmailAndIsActiveTrue(dto.getEmail()) ||
                    repository.existsByPhoneAndIsActiveTrue(dto.getPhone())) {
                throw new ValidationException(AaqilConstants.AAQIL_ALREADY_EXISTS);
            }

        } else {

            if (repository.existsByEmailAndIsActiveTrueAndIdNot(dto.getEmail(), dto.getId()) ||
                    repository.existsByPhoneAndIsActiveTrueAndIdNot(dto.getPhone(), dto.getId())) {
                throw new ValidationException(AaqilConstants.AAQIL_ALREADY_EXISTS);
            }
        }
    }

    // ------------------------------------------------------------------
    // Private Helpers
    // ------------------------------------------------------------------

    private Aaqil findByIdAndNotDeleted(Long id) {
        return repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(AaqilConstants.AAQIL_NOT_FOUND + id));
    }

    private Aaqil findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(AaqilConstants.AAQIL_NOT_FOUND + id));
    }
}

package com.nec.middleware.hr.service.impl;

import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.hr.constant.UniversityTraineeConstants;
import com.nec.middleware.hr.dto.request.UniversityTraineeListRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.entity.UniversityTrainee;
import com.nec.middleware.hr.mapper.UniversityTraineeMapper;
import com.nec.middleware.hr.repository.UniversityTraineeRepository;
import com.nec.middleware.hr.service.UniversityTraineeService;
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
public class UniversityTraineeServiceImpl implements UniversityTraineeService {

    private static final String CODE_PREFIX = "UT";
    private static final int    CODE_PAD    = 3;   // UT001, UT002 …

    private final UniversityTraineeRepository repository;
    private final UniversityTraineeMapper      mapper;

    // ------------------------------------------------------------------ SAVE / UPDATE

    @Override
    @Transactional
    public UniversityTraineeResponseDto saveOrUpdate(UniversityTraineeRequestDto requestDto) {

        validateTrainee(requestDto);
        UniversityTrainee entity;

        if (requestDto.getId() == null) {

            log.info("Creating university trainee");
            entity = mapper.toEntity(requestDto);
            entity.setCode(generateCode());

        } else {

            log.info("Updating university trainee id: {}", requestDto.getId());
            entity = findByIdAndNotDeleted(requestDto.getId());
            mapper.updateEntity(entity, requestDto);
            // code is never changed on update
        }

        UniversityTrainee saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public UniversityTraineeResponseDto getById(Long id) {
        return mapper.toResponseDto(findByIdAndNotDeleted(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UniversityTraineeResponseDto> getAll(UniversityTraineeListRequestDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository.findAllWithFilters(
                filterDto.getUniversityId(),
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
    public UniversityTraineeResponseDto changeStatus(Long id) {

        UniversityTrainee entity = findById(id);
        boolean newStatus = !entity.getIsActive();
        entity.setIsActive(newStatus);

        if (newStatus) {
            entity.setIsDeleted(false);
        }

        log.info("Changing status for university trainee id: {} → isActive={}", id, newStatus);

        return mapper.toResponseDto(repository.save(entity));
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public void softDelete(Long id) {

        UniversityTrainee entity = findById(id);
        entity.setIsDeleted(true);
        entity.setIsActive(false);

        repository.save(entity);
        log.info("University trainee soft deleted with id: {}", id);
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

    private void validateTrainee(UniversityTraineeRequestDto dto) {

        if (dto.getId() == null) {

            if (repository.existsByEmailAndIsDeletedFalse(dto.getEmail()) ||
                    repository.existsByPhoneAndIsDeletedFalse(dto.getPhone())) {
                throw new ValidationException(UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS);
            }

        } else {

            if (repository.existsByEmailAndIsDeletedFalseAndIdNot(dto.getEmail(), dto.getId()) ||
                    repository.existsByPhoneAndIsDeletedFalseAndIdNot(dto.getPhone(), dto.getId())) {
                throw new ValidationException(UniversityTraineeConstants.TRAINEE_ALREADY_EXISTS);
            }
        }
    }

    // ------------------------------------------------------------------
    // Private Helpers
    // ------------------------------------------------------------------

    private UniversityTrainee findByIdAndNotDeleted(Long id) {
        return repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(UniversityTraineeConstants.TRAINEE_NOT_FOUND + id));
    }

    private UniversityTrainee findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(UniversityTraineeConstants.TRAINEE_NOT_FOUND + id));
    }
}

package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.TrainingManagementStatusRepository;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.hr.constant.TrainingTraineeAllocationConstants;
import com.nec.middleware.hr.dto.request.TrainingTraineeAllocationFilterRequestDto;
import com.nec.middleware.hr.dto.request.TrainingTraineeAllocationRequestDto;
import com.nec.middleware.hr.dto.response.TrainingTraineeAllocationResponseDto;
import com.nec.middleware.hr.entity.TrainingTraineeAllocation;
import com.nec.middleware.hr.mapper.TrainingTraineeAllocationMapper;
import com.nec.middleware.hr.repository.TrainingClassRepository;
import com.nec.middleware.hr.repository.TrainingTraineeAllocationRepository;
import com.nec.middleware.hr.repository.UniversityTraineeRepository;
import com.nec.middleware.hr.service.TrainingTraineeAllocationService;
import com.nec.middleware.hr.specification.TrainingTraineeAllocationSearchSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingTraineeAllocationServiceImpl
        implements TrainingTraineeAllocationService {

    private final TrainingTraineeAllocationRepository allocationRepository;
    private final TrainingTraineeAllocationMapper allocationMapper;

    private final UniversityTraineeRepository universityTraineeRepository;
    private final TrainingClassRepository trainingClassRepository;
    private final TrainingManagementStatusRepository statusRepository;

    @Override
    @Transactional
    public TrainingTraineeAllocationResponseDto createAllocation(
            TrainingTraineeAllocationRequestDto requestDto) {

        log.info("Creating trainee allocation");

        if (allocationRepository.existsByTraineeIdAndIsActiveTrue(
                requestDto.getTraineeId())) {
            throw new ValidationException(
                    TrainingTraineeAllocationConstants.TRAINEE_ALREADY_ALLOCATED
            );
        }

        TrainingTraineeAllocation entity = new TrainingTraineeAllocation();

        entity.setAllocationCode(
                generateAllocationCode(
                        TrainingTraineeAllocationConstants.CODE_PREFIX,
                        TrainingTraineeAllocationConstants.CODE_PAD
                )
        );

        populateAllocation(entity, requestDto);

        TrainingTraineeAllocation savedEntity =
                allocationRepository.save(entity);

        return allocationMapper.toTrainingTraineeAllocationResponse(savedEntity);
    }

    @Override
    @Transactional
    public TrainingTraineeAllocationResponseDto updateAllocation(
            String allocationCode,
            TrainingTraineeAllocationRequestDto requestDto) {

        log.info("Updating trainee allocation: {}", allocationCode);

        TrainingTraineeAllocation entity =
                findByAllocationCode(allocationCode);

        if (allocationRepository.existsByTraineeIdAndIsActiveTrueAndIdNot(
                requestDto.getTraineeId(),
                entity.getId())) {
            throw new ValidationException(
                    TrainingTraineeAllocationConstants.TRAINEE_ALREADY_ALLOCATED
            );
        }

        populateAllocation(entity, requestDto);

        TrainingTraineeAllocation updatedEntity =
                allocationRepository.save(entity);

        return allocationMapper.toTrainingTraineeAllocationResponse(updatedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingTraineeAllocationResponseDto getAllocationByAllocationCode(
            String allocationCode) {

        return allocationMapper.toTrainingTraineeAllocationResponse(
                findByAllocationCode(allocationCode)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingTraineeAllocationResponseDto> getAllAllocations(
            TrainingTraineeAllocationFilterRequestDto filterDto,
            int pageNumber,
            int pageSize) {

        if (filterDto == null) {
            filterDto = new TrainingTraineeAllocationFilterRequestDto();
        }

        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return allocationRepository.findAll(
                        TrainingTraineeAllocationSearchSpecification
                                .buildSpecification(filterDto),
                        pageable
                )
                .map(allocationMapper::toTrainingTraineeAllocationResponse);
    }

    @Override
    @Transactional
    public TrainingTraineeAllocationResponseDto changeStatus(
            String allocationCode,
            Boolean isActive) {

        TrainingTraineeAllocation entity =
                findByAllocationCode(allocationCode);

        entity.setIsActive(isActive);

        log.info("Changing allocation status: {} -> isActive={}",
                allocationCode,
                isActive);

        return allocationMapper.toTrainingTraineeAllocationResponse(
                allocationRepository.save(entity)
        );
    }

    private void populateAllocation(
            TrainingTraineeAllocation entity,
            TrainingTraineeAllocationRequestDto requestDto) {

        entity.setAllocationDate(requestDto.getAllocationDate());
        entity.setNotes(requestDto.getNotes());

        entity.setTrainee(
                universityTraineeRepository.findById(requestDto.getTraineeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Trainee not found"))
        );

        entity.setTrainingClass(
                trainingClassRepository.findById(requestDto.getTrainingClassId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Training class not found"))
        );

        entity.setStatus(
                statusRepository.findById(requestDto.getStatusId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Status not found"))
        );
    }

    private String generateAllocationCode(String codePrefix, int codePad) {
        int next = allocationRepository.findMaxCodeSequence() + 1;
        return codePrefix + String.format("%0" + codePad + "d", next);
    }

    private TrainingTraineeAllocation findByAllocationCode(
            String allocationCode) {

        return allocationRepository.findByAllocationCode(allocationCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                TrainingTraineeAllocationConstants.ALLOCATION_NOT_FOUND
                                        + allocationCode
                        ));
    }
}
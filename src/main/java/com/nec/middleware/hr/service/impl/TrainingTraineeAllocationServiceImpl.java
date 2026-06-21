package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.TrainingManagementStatusRepository;
import com.nec.middleware.Lookups.repository.TrainingTypeRepository;
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
import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
import com.nec.middleware.masterdata.repository.CityRepository;
import com.nec.middleware.masterdata.repository.DistrictRepository;
import com.nec.middleware.masterdata.repository.RegionRepository;
import com.nec.middleware.masterdata.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

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
    private final UniqueIdGeneratorService uniqueIdGeneratorService;

    private final RegionRepository regionRepository;

    private final DistrictRepository districtRepository;

    private final CityRepository cityRepository;

    private final UniversityRepository universityRepository;

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    @Transactional
    public TrainingTraineeAllocationResponseDto createTraineeAllocation(
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
                generateAllocationCode()
        );

        populateAllocation(entity, requestDto);

        TrainingTraineeAllocation savedEntity =
                allocationRepository.save(entity);

        return allocationMapper.toTrainingTraineeAllocationResponse(savedEntity);
    }

    @Override
    @Transactional
    public TrainingTraineeAllocationResponseDto updateTraineeAllocation(
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
    public Page<TrainingTraineeAllocationResponseDto> getAllTraineeAllocations(
            TrainingTraineeAllocationFilterRequestDto request,
            int page,
            int size) {

        if(request==null)
            request=new TrainingTraineeAllocationFilterRequestDto();
        Pageable pageable = PageRequest.of(page, size);

        return allocationRepository.findAll(
                TrainingTraineeAllocationSearchSpecification.buildSpecification(request),pageable)
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

        entity.setRegion(
                regionRepository.findById(requestDto.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Region not found"))
        );

        entity.setDistrict(
                districtRepository.findById(requestDto.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "District not found"))
        );

        entity.setCity(
                cityRepository.findById(requestDto.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "City not found"))
        );

        entity.setUniversity(
                universityRepository.findById(requestDto.getUniversityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "University not found"))
        );

        entity.setTrainee(
                universityTraineeRepository.findById(requestDto.getTraineeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Trainee not found"))
        );

        entity.setFaculty(
                requestDto.getFaculty()
        );

        entity.setTrainingClass(
                trainingClassRepository.findById(
                                requestDto.getTrainingClassId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Training class not found"))
        );

        // Add this here
        entity.setTrainingType(
                trainingTypeRepository.findById(
                                requestDto.getTrainingTypeId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Training type not found"))
        );

        entity.setStatus(
                statusRepository.findById(requestDto.getStatusId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Status not found"))
        );

        entity.setAllocationDate(
                requestDto.getAllocationDate()
        );

        entity.setNotes(
                requestDto.getNotes()
        );
    }

    private String generateAllocationCode() {
       String prefix= TrainingTraineeAllocationConstants.CODE_PREFIX+"-"
               + Year.now().getValue()+"-";
       return uniqueIdGeneratorService.generateId(
               ModuleCode.TRAINEE_ALLOCATION,
               prefix
       );
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
package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.TrainingManagementStatusRepository;
import com.nec.middleware.Lookups.repository.TrainingTypeRepository;
import com.nec.middleware.exception.ResourceAlreadyExistsException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.constant.TrainingClassConstants;
import com.nec.middleware.hr.dto.request.TrainingClassListRequestDto;
import com.nec.middleware.hr.dto.request.TrainingClassRequest;
import com.nec.middleware.hr.dto.response.TrainingClassResponse;
import com.nec.middleware.hr.entity.TrainingClass;
import com.nec.middleware.hr.mapper.TrainingClassMapper;
import com.nec.middleware.hr.repository.TrainingClassRepository;
import com.nec.middleware.hr.service.TrainingClassService;
import com.nec.middleware.hr.specification.TrainingClassSearchSpecification;
import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
import com.nec.middleware.masterdata.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingClassServiceImpl implements TrainingClassService {

    private static final Short NOT_DELETED_SHORT = 0;
    private static final Boolean NOT_DELETED_BOOLEAN = false;

    private final TrainingClassRepository trainingClassRepository;
    private final TrainingClassMapper trainingClassMapper;

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingManagementStatusRepository trainingManagementStatusRepository;

    private final MasterDataRepository masterDataRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;
    private final UniversityRepository universityRepository;
    private final HrTrainerTotRepository hrTrainerTotRepository;
    private final UniqueIdGeneratorService uniqueIdGeneratorService;

    @Override
    public TrainingClassResponse createTrainingClass(TrainingClassRequest request) {

        if (trainingClassRepository.existsByClassNameIgnoreCase(request.getClassName())) {
            throw new ResourceAlreadyExistsException("Training class name already exists");
        }

        TrainingClass savedEntity = trainingClassRepository.save(toTrainingClassEntity(request));
        return trainingClassMapper.toTrainingClassResponse(savedEntity);
    }


    @Override
    public TrainingClassResponse updateTrainingClass(String classCode, TrainingClassRequest request) {

        TrainingClass trainingClass = trainingClassRepository.findByClassCode(classCode)
                .orElseThrow(() -> new ResourceNotFoundException("Training class not found"));

        //this check is to ensure that when updating,
        //if in case the the classname is already assigned to another code no updation should happen
        if (trainingClassRepository.existsByClassNameIgnoreCaseAndClassCodeNot(
                request.getClassName(),
                classCode
        )) {
            throw new ResourceAlreadyExistsException("Training class name already exists");
        }

        populateTrainingClass(trainingClass, request);

        TrainingClass updatedEntity = trainingClassRepository.save(trainingClass);

        return trainingClassMapper.toTrainingClassResponse(updatedEntity);
    }

    @Override
    public TrainingClassResponse getTrainingClassByClassCode(String classCode) {

        TrainingClass entity = trainingClassRepository.findByClassCode(classCode)
                .orElseThrow(() -> new ResourceNotFoundException("Training class not found"));

        return trainingClassMapper.toTrainingClassResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingClassResponse> getAllTrainingClasses(
            TrainingClassListRequestDto request,
            int page,
            int size) {

        if (request == null) {
            request = new TrainingClassListRequestDto();
        }

        log.info("classCode = {}", request.getClassCode());
        log.info("className = {}", request.getClassName());

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return trainingClassRepository.findAll(
                        TrainingClassSearchSpecification.build(request),
                        pageable
                )
                .map(trainingClassMapper::toTrainingClassResponse);
    }
    @Override
    public TrainingClassResponse updateTrainingClassStatus(String classCode, Boolean isActive) {

        TrainingClass entity = trainingClassRepository.findByClassCode(classCode)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        entity.setIsActive(isActive);

        TrainingClass updatedEntity = trainingClassRepository.save(entity);

        return trainingClassMapper.toTrainingClassResponse(updatedEntity);
    }

    private void populateTrainingClass(
            TrainingClass entity,
            TrainingClassRequest request
    ) {

        //are these fileds compulsory sent in update request..?
        entity.setClassName(request.getClassName());
        entity.setCapacity(request.getCapacity());
        entity.setLocation(request.getLocation());
        entity.setDescription(request.getDescription());
        entity.setPreRequests(request.getPreRequests());
        entity.setUpdatedBy(request.getUpdatedBy());

        if (request.getTrainingTypeId() != null) {
            entity.setTrainingType(
                    trainingTypeRepository.findById(request.getTrainingTypeId())
                            .orElseThrow(() -> new RuntimeException("Training type not found"))
            );
        }

        if (request.getRegionId() != null) {
            entity.setRegion(
                    masterDataRepository.findByIdAndIsDeleted(request.getRegionId(), NOT_DELETED_SHORT)
                            .orElseThrow(() -> new RuntimeException("Region not found"))
            );
        }

        if (request.getDistrictId() != null) {
            entity.setDistrict(
                    districtRepository.findByIdAndIsDeleted(request.getDistrictId(), NOT_DELETED_SHORT)
                            .orElseThrow(() -> new RuntimeException("District not found"))
            );
        }

        if (request.getCityId() != null) {
            entity.setCity(
                    cityRepository.findByIdAndIsDeleted(request.getCityId(), NOT_DELETED_SHORT)
                            .orElseThrow(() -> new RuntimeException("City not found"))
            );
        }

        if (request.getUniversityId() != null) {
            entity.setUniversity(
                    universityRepository.findByIdAndIsDeleted(request.getUniversityId(), NOT_DELETED_SHORT)
                            .orElseThrow(() -> new RuntimeException("University not found"))
            );
        }

        if (request.getTrainerTotId() != null) {
            entity.setTrainerTot(
                    hrTrainerTotRepository.findByIdAndIsDeleted(
                                    request.getTrainerTotId(),
                                    NOT_DELETED_BOOLEAN
                            )
                            .orElseThrow(() -> new RuntimeException("Trainer TOT not found"))
            );
        }

        if (request.getStatusId() != null) {
            entity.setStatus(
                    trainingManagementStatusRepository.findById(request.getStatusId())
                            .orElseThrow(() -> new RuntimeException("Training status not found"))
            );
        }

    }


    private TrainingClass toTrainingClassEntity(TrainingClassRequest request) {

        TrainingClass trainingClassEntity = trainingClassMapper.toTrainingClassEntity(request);
        trainingClassEntity.setClassCode(generateTrainingClassNumber());
        trainingClassEntity.setTrainingType(
                trainingTypeRepository.findById(request.getTrainingTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Training type not found"))
        );

        trainingClassEntity.setStatus(
                trainingManagementStatusRepository.findById(request.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException("Training status not found"))
        );

        trainingClassEntity.setRegion(
                masterDataRepository.findByIdAndIsDeleted(request.getRegionId(), NOT_DELETED_SHORT)
                        .orElseThrow(() -> new ResourceNotFoundException("Region not found"))
        );

        trainingClassEntity.setDistrict(
                districtRepository.findByIdAndIsDeleted(request.getDistrictId(), NOT_DELETED_SHORT)
                        .orElseThrow(() -> new ResourceNotFoundException("District not found"))
        );

        trainingClassEntity.setCity(
                cityRepository.findByIdAndIsDeleted(request.getCityId(), NOT_DELETED_SHORT)
                        .orElseThrow(() -> new ResourceNotFoundException("City not found"))
        );

        trainingClassEntity.setUniversity(
                universityRepository.findByIdAndIsDeleted(request.getUniversityId(), NOT_DELETED_SHORT)
                        .orElseThrow(() -> new ResourceNotFoundException("University not found"))
        );

        trainingClassEntity.setTrainerTot(
                hrTrainerTotRepository.findByIdAndIsDeleted(
                                request.getTrainerTotId(),
                                NOT_DELETED_BOOLEAN
                        )
                        .orElseThrow(() -> new ResourceNotFoundException("Trainer TOT not found"))
        );

        return trainingClassEntity;
    }


    public String generateTrainingClassNumber() {

        String prefix = TrainingClassConstants.CODE_PREFIX + "-"
                + Year.now().getValue() + "-";

        return uniqueIdGeneratorService.generateId(
                ModuleCode.TRAINING_CLASS,
                prefix
        );
    }

}
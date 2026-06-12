package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.TrainingManagementStatusRepository;
import com.nec.middleware.Lookups.repository.TrainingTypeRepository;
import com.nec.middleware.hr.constant.TrainingClassConstants;
import com.nec.middleware.hr.dto.request.TrainingClassListRequestDto;
import com.nec.middleware.hr.dto.request.TrainingClassRequest;
import com.nec.middleware.hr.dto.response.TrainingClassResponse;
import com.nec.middleware.hr.entity.TrainingClass;
import com.nec.middleware.hr.mapper.TrainingClassMapper;
import com.nec.middleware.hr.repository.TrainingClassRepository;
import com.nec.middleware.hr.service.TrainingClassService;
import com.nec.middleware.hr.specification.TrainingClassSearchSpecification;
import com.nec.middleware.masterdata.repository.CityRepository;
import com.nec.middleware.masterdata.repository.DistrictRepository;
import com.nec.middleware.masterdata.repository.HrTrainerTotRepository;
import com.nec.middleware.masterdata.repository.MasterDataRepository;
import com.nec.middleware.masterdata.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Override
    public TrainingClassResponse create(TrainingClassRequest request) {

        if (trainingClassRepository.existsByClassNameIgnoreCaseAndIsActiveTrue(request.getClassName())) {
            throw new RuntimeException("Training class name already exists");
        }

        TrainingClass entity = new TrainingClass();

        entity.setClassCode(
                generateClassCode(
                        TrainingClassConstants.CODE_PREFIX,
                        TrainingClassConstants.CODE_PAD
                )
        );

        populateTrainingClass(entity, request);

        TrainingClass savedEntity = trainingClassRepository.save(entity);

        return trainingClassMapper.toResponse(savedEntity);
    }

    @Override
    public TrainingClassResponse update(String classCode, TrainingClassRequest request) {

        TrainingClass entity = trainingClassRepository.findByClassCodeAndIsActiveTrue(classCode)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        if (trainingClassRepository.existsByClassNameIgnoreCaseAndIsActiveTrueAndClassCodeNot(
                request.getClassName(),
                classCode
        )) {
            throw new RuntimeException("Training class name already exists");
        }

        populateTrainingClass(entity, request);

        TrainingClass updatedEntity = trainingClassRepository.save(entity);

        return trainingClassMapper.toResponse(updatedEntity);
    }

    @Override
    public TrainingClassResponse getByClassCode(String classCode) {

        TrainingClass entity = trainingClassRepository.findByClassCodeAndIsActiveTrue(classCode)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        return trainingClassMapper.toResponse(entity);
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
                .map(trainingClassMapper::toResponse);
    }
    @Override
    public TrainingClassResponse updateStatus(String classCode, Boolean isActive) {

        TrainingClass entity = trainingClassRepository.findByClassCode(classCode)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        entity.setIsActive(isActive);

        TrainingClass updatedEntity = trainingClassRepository.save(entity);

        return trainingClassMapper.toResponse(updatedEntity);
    }

    private void populateTrainingClass(
            TrainingClass entity,
            TrainingClassRequest request
    ) {

        entity.setClassName(request.getClassName());
        entity.setCapacity(request.getCapacity());
        entity.setLocation(request.getLocation());
        entity.setDescription(request.getDescription());
        entity.setPreRequests(request.getPreRequests());

        entity.setTrainingType(
                trainingTypeRepository.findById(request.getTrainingTypeId())
                        .orElseThrow(() -> new RuntimeException("Training type not found"))
        );

        entity.setStatus(
                trainingManagementStatusRepository.findById(request.getStatusId())
                        .orElseThrow(() -> new RuntimeException("Training status not found"))
        );

        entity.setRegion(
                masterDataRepository.findByIdAndIsDeleted(request.getRegionId(), NOT_DELETED_SHORT)
                        .orElseThrow(() -> new RuntimeException("Region not found"))
        );

        entity.setDistrict(
                districtRepository.findByIdAndIsDeleted(request.getDistrictId(), NOT_DELETED_SHORT)
                        .orElseThrow(() -> new RuntimeException("District not found"))
        );

        entity.setCity(
                cityRepository.findByIdAndIsDeleted(request.getCityId(), NOT_DELETED_SHORT)
                        .orElseThrow(() -> new RuntimeException("City not found"))
        );

        entity.setUniversity(
                universityRepository.findByIdAndIsDeleted(request.getUniversityId(), NOT_DELETED_SHORT)
                        .orElseThrow(() -> new RuntimeException("University not found"))
        );

        entity.setTrainerTot(
                hrTrainerTotRepository.findByIdAndIsDeleted(
                                request.getTrainerTotId(),
                                NOT_DELETED_BOOLEAN
                        )
                        .orElseThrow(() -> new RuntimeException("Trainer TOT not found"))
        );
    }

    private String generateClassCode(
            String codePrefix,
            int codePad
    ) {
        int next = trainingClassRepository.findMaxCodeSequence() + 1;

        return codePrefix + String.format("%0" + codePad + "d", next);
    }
}
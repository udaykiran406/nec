package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.repository.TrainingManagementStatusRepository;
import com.nec.middleware.Lookups.repository.TrainingTypeRepository;
import com.nec.middleware.hr.dto.request.TrainingClassRequest;
import com.nec.middleware.hr.dto.response.TrainingClassResponse;
import com.nec.middleware.hr.entity.TrainingClass;
import com.nec.middleware.hr.mapper.TrainingClassMapper;
import com.nec.middleware.hr.repository.TrainingClassRepository;
import com.nec.middleware.hr.service.TrainingClassService;
import com.nec.middleware.masterdata.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingClassServiceImpl implements TrainingClassService {

    private static final Short NOT_DELETED = 0;
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
    public TrainingClassResponse save(TrainingClassRequest request) {

        TrainingClass entity;

        if (request.getId() == null) {
            if (trainingClassRepository.existsByClassNameIgnoreCaseAndIsDeletedFalse(request.getClassName())) {
                throw new RuntimeException("Training class name already exists");
            }
            entity = new TrainingClass();
        } else {
            entity = trainingClassRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Training class not found"));

            if (Boolean.TRUE.equals(entity.getIsDeleted())) {
                throw new RuntimeException("Training class already deleted");
            }
        }

        entity.setClassName(request.getClassName());
        entity.setCapacity(request.getCapacity());
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
                masterDataRepository.findByIdAndIsDeleted(request.getRegionId(), NOT_DELETED)
                        .orElseThrow(() -> new RuntimeException("Region not found"))
        );

        entity.setDistrict(
                districtRepository.findByIdAndIsDeleted(request.getDistrictId(), NOT_DELETED)
                        .orElseThrow(() -> new RuntimeException("District not found"))
        );

        entity.setCity(
                cityRepository.findByIdAndIsDeleted(request.getCityId(), NOT_DELETED)
                        .orElseThrow(() -> new RuntimeException("City not found"))
        );

        entity.setUniversity(
                universityRepository.findByIdAndIsDeleted(request.getUniversityId(), NOT_DELETED)
                        .orElseThrow(() -> new RuntimeException("University not found"))
        );

        entity.setTrainerTot(
                hrTrainerTotRepository.findByIdAndIsDeleted(
                                request.getTrainerTotId(),
                                NOT_DELETED_BOOLEAN
                        )
                        .orElseThrow(() -> new RuntimeException("Trainer TOT not found"))
        );
        TrainingClass savedEntity = trainingClassRepository.save(entity);

        return trainingClassMapper.toResponse(savedEntity);
    }

    @Override
    public TrainingClassResponse getById(Long id) {
        TrainingClass entity = trainingClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        if (Boolean.TRUE.equals(entity.getIsDeleted())) {
            throw new RuntimeException("Training class not found");
        }

        return trainingClassMapper.toResponse(entity);
    }

    @Override
    public List<TrainingClassResponse> getAll() {
        return trainingClassRepository.findByIsDeletedFalse()
                .stream()
                .map(trainingClassMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        TrainingClass entity = trainingClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training class not found"));

        entity.setIsDeleted(Boolean.TRUE);
        entity.setIsActive(Boolean.FALSE);

        trainingClassRepository.save(entity);
    }
}
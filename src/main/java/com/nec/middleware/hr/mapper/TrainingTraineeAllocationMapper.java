package com.nec.middleware.hr.mapper;

import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.hr.dto.response.TrainingTraineeAllocationResponseDto;
import com.nec.middleware.hr.entity.TrainingTraineeAllocation;
import org.springframework.stereotype.Component;
@Component
public class TrainingTraineeAllocationMapper {

    public TrainingTraineeAllocationResponseDto
    toTrainingTraineeAllocationResponse(
            TrainingTraineeAllocation entity) {

        if (entity == null) {
            return null;
        }

        return TrainingTraineeAllocationResponseDto.builder()

                .id(entity.getId())
                .allocationCode(entity.getAllocationCode())

                .trainee(entity.getTrainee() != null
                        ? IdValueDto.builder()
                        .id(entity.getTrainee().getId())
                        .value(entity.getTrainee().getFullName())
                        .build()
                        : null)

                .university(entity.getUniversity() != null
                        ? IdValueDto.builder()
                        .id(entity.getUniversity().getId())
                        .value(entity.getUniversity().getUniversityName())
                        .build()
                        : null)

                .region(entity.getRegion() != null
                        ? IdValueDto.builder()
                        .id(entity.getRegion().getId())
                        .value(entity.getRegion().getRegionName())
                        .build()
                        : null)

                .district(entity.getDistrict() != null
                        ? IdValueDto.builder()
                        .id(entity.getDistrict().getId())
                        .value(entity.getDistrict().getDistrictName())
                        .build()
                        : null)

                .city(entity.getCity() != null
                        ? IdValueDto.builder()
                        .id(entity.getCity().getId())
                        .value(entity.getCity().getCityName())
                        .build()
                        : null)

                .trainingClass(entity.getTrainingClass() != null
                        ? IdValueDto.builder()
                        .id(entity.getTrainingClass().getId())
                        .value(entity.getTrainingClass().getClassName())
                        .build()
                        : null)

                .trainingType(entity.getTrainingType() != null
                        ? IdValueDto.builder()
                        .id(entity.getTrainingType().getId())
                        .value(entity.getTrainingType().getValue())
                        .build()
                        : null)

                .status(entity.getStatus() != null
                        ? IdValueDto.builder()
                        .id(entity.getStatus().getId())
                        .value(entity.getStatus().getValue())
                        .build()
                        : null)

                .faculty(entity.getFaculty())
                .allocationDate(entity.getAllocationDate())
                .notes(entity.getNotes())
                .isActive(entity.getIsActive())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())

                .build();
    }
}
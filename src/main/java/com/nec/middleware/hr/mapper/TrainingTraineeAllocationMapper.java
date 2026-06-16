package com.nec.middleware.hr.mapper;

import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.hr.dto.response.TrainingTraineeAllocationResponseDto;
import com.nec.middleware.hr.entity.TrainingTraineeAllocation;
import org.springframework.stereotype.Component;

@Component
public class TrainingTraineeAllocationMapper {

    public TrainingTraineeAllocationResponseDto toTrainingTraineeAllocationResponse(
            TrainingTraineeAllocation entity) {

        if (entity == null) {
            return null;
        }

        return TrainingTraineeAllocationResponseDto.builder()
                .id(entity.getId())
                .allocationCode(entity.getAllocationCode())
                .allocationDate(entity.getAllocationDate())
                .notes(entity.getNotes())

                .trainee(IdValueDto.builder()
                        .id(entity.getTrainee().getId())
                        .value(entity.getTrainee().getFullName())
                        .build())

                .university(IdValueDto.builder()
                        .id(entity.getTrainee().getUniversity().getId())
                        .value(entity.getTrainee().getUniversity().getUniversityName())
                        .build())

                .region(IdValueDto.builder()
                        .id(entity.getTrainee().getRegion().getId())
                        .value(entity.getTrainee().getRegion().getRegionName())
                        .build())

                .faculty(entity.getTrainee().getFaculty())

                .trainingClass(IdValueDto.builder()
                        .id(entity.getTrainingClass().getId())
                        .value(entity.getTrainingClass().getClassName())
                        .build())

                .trainingType(IdValueDto.builder()
                        .id(entity.getTrainingClass().getTrainingType().getId())
                        .value(entity.getTrainingClass().getTrainingType().getValue())
                        .build())

                .status(IdValueDto.builder()
                        .id(entity.getStatus().getId())
                        .value(entity.getStatus().getValue())
                        .build())

                .isActive(entity.getIsActive())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
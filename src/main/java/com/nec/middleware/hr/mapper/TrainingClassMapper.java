package com.nec.middleware.hr.mapper;

import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.hr.dto.request.TrainingClassRequest;
import com.nec.middleware.hr.dto.response.TrainingClassResponse;
import com.nec.middleware.hr.entity.TrainingClass;
import org.springframework.stereotype.Component;

@Component
public class TrainingClassMapper {

    public TrainingClass toTrainingClassEntity(TrainingClassRequest request) {
        if (request == null) {
            return null;
        }
        TrainingClass trainingClassEntity = TrainingClass.builder()
                .className(request.getClassName())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .description(request.getDescription())
                .preRequests(request.getPreRequests())
                .build();

        trainingClassEntity.setIsActive(Boolean.TRUE);
        trainingClassEntity.setCreatedBy(request.getCreatedBy());
        trainingClassEntity.setUpdatedBy(request.getCreatedBy());

        return trainingClassEntity;
    }


    public TrainingClassResponse toTrainingClassResponse(TrainingClass entity) {

        //no need of this null check , we are finding the record based on classCode and if not found we are throwing exception,
        // so this method will never be called .
        if (entity == null) {
            return null;
        }

        return TrainingClassResponse.builder()
                .classCode(entity.getClassCode())
                .className(entity.getClassName())
                .capacity(entity.getCapacity())
                .location(entity.getLocation())
                .description(entity.getDescription())
                .preRequests(entity.getPreRequests())

                .trainingType(entity.getTrainingType() != null ?
                        IdValueDto.builder()
                                .id(entity.getTrainingType().getId())
                                .value(entity.getTrainingType().getValue()).build() : null)
                .region(entity.getRegion() != null ?
                        IdValueDto.builder()
                                .id(entity.getRegion().getId())
                                .value(entity.getRegion().getRegionName()).build() : null)
                .district(entity.getDistrict() != null ?
                        IdValueDto.builder()
                                .id(entity.getDistrict().getId())
                                .value(entity.getDistrict().getDistrictName()).build() : null)

                .city(entity.getCity() != null ?
                        IdValueDto.builder()
                                .id(entity.getCity().getId())
                                .value(entity.getCity().getCityName()).build() : null)
                .university(entity.getUniversity() != null ?
                        IdValueDto.builder()
                                .id(entity.getUniversity().getId())
                                .value(entity.getUniversity().getUniversityName()).build() : null)
                .trainerTot(entity.getTrainerTot() != null ?
                        IdValueDto.builder()
                                .id(entity.getTrainerTot().getId())
                                .value(entity.getTrainerTot().getFullName()).build() : null)
                .status(entity.getStatus() != null ?
                        IdValueDto.builder()
                                .id(entity.getStatus().getId())
                                .value(entity.getStatus().getValue()).build() : null)

                .isActive(entity.getIsActive())

                //why to send these fields as these are timings .
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())

                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())

                .build();
    }
}
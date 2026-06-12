package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.response.TrainingClassResponse;
import com.nec.middleware.hr.entity.TrainingClass;
import org.springframework.stereotype.Component;

@Component
public class TrainingClassMapper {

    public TrainingClassResponse toResponse(TrainingClass entity) {

        if (entity == null) {
            return null;
        }

        return TrainingClassResponse.builder()
                .id(entity.getId())
                .className(entity.getClassName())
                .capacity(entity.getCapacity())

                .trainingTypeId(entity.getTrainingTypeId())
                .trainingTypeName(entity.getTrainingType().getValue())

                .regionId(entity.getRegionId())
                .regionName(entity.getRegion().getRegionName())

                .districtId(entity.getDistrictId())
                .districtName(entity.getDistrict().getDistrictName())

                .cityId(entity.getCityId())
                .cityName(entity.getCity().getCityName())

                .universityId(entity.getUniversityId())
                .universityName(entity.getUniversity().getUniversityName())

                .trainerTotId(entity.getTrainerTotId())
                .trainerTotName(entity.getTrainerTot().getFullName())

                .statusId(entity.getStatusId())
                .statusName(entity.getStatus().getValue())

                .isActive(entity.getIsActive())
                .isDeleted(entity.getIsDeleted())

                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
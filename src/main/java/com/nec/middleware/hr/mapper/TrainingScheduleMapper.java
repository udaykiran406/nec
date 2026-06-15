package com.nec.middleware.hr.mapper;

import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.hr.dto.response.TrainingScheduleResponseDto;
import com.nec.middleware.hr.entity.TrainingSchedule;
import org.springframework.stereotype.Component;

@Component
public class TrainingScheduleMapper {

    public TrainingScheduleResponseDto toTrainingScheduleResponse(
            TrainingSchedule entity) {

        if (entity == null) {
            return null;
        }

        return TrainingScheduleResponseDto.builder()
                .id(entity.getId())
                .scheduleCode(entity.getScheduleCode())

                .trainingClass(IdValueDto.builder()
                        .id(entity.getTrainingClass().getId())
                        .value(entity.getTrainingClass().getClassName())
                        .build())

                .fromDate(entity.getFromDate())
                .toDate(entity.getToDate())
                .duration(entity.getDuration())
                .timeSlot(entity.getTimeSlot())
                .venue(entity.getVenue())
                .location(entity.getLocation())

                .isActive(entity.getIsActive())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
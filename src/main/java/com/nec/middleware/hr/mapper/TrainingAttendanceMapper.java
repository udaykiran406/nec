package com.nec.middleware.hr.mapper;

import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.hr.dto.response.AttendanceRecordResponseDto;
import com.nec.middleware.hr.dto.response.TrainingAttendanceResponseDto;
import com.nec.middleware.hr.entity.TrainingAttendanceRecord;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingAttendanceMapper {

    public TrainingAttendanceResponseDto toTrainingAttendanceResponse(
            List<TrainingAttendanceRecord> records) {

        if (records == null || records.isEmpty()) {
            return null;
        }

        TrainingAttendanceRecord firstRecord = records.get(0);

        return TrainingAttendanceResponseDto.builder()
                .trainingClass(IdValueDto.builder()
                        .id(firstRecord.getTrainingClass().getId())
                        .value(firstRecord.getTrainingClass().getClassName())
                        .build())
                .attendanceDate(firstRecord.getAttendanceDate())
                .signedSheetUrl(firstRecord.getSignedSheetUrl())
                .attendanceRecords(
                        records.stream()
                                .map(this::toAttendanceRecordResponse)
                                .toList()
                )
                .build();
    }

    public AttendanceRecordResponseDto toAttendanceRecordResponse(
            TrainingAttendanceRecord entity) {

        if (entity == null) {
            return null;
        }

        return AttendanceRecordResponseDto.builder()
                .trainee(IdValueDto.builder()
                        .id(entity.getTrainee().getId())
                        .value(entity.getTrainee().getFullName())
                        .build())
                .isPresent(entity.getIsPresent())
                .build();
    }
}
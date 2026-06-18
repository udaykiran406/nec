package com.nec.middleware.hr.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nec.middleware.exception.ResourceAlreadyExistsException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.constant.TrainingAttendanceConstants;
import com.nec.middleware.hr.dto.request.AttendanceRecordRequestDto;
import com.nec.middleware.hr.dto.request.TrainingAttendanceFilterRequestDto;
import com.nec.middleware.hr.dto.response.TrainingAttendanceResponseDto;
import com.nec.middleware.hr.entity.TrainingAttendanceRecord;
import com.nec.middleware.hr.entity.TrainingClass;
import com.nec.middleware.hr.entity.TrainingSchedule;
import com.nec.middleware.hr.entity.UniversityTrainee;
import com.nec.middleware.hr.mapper.TrainingAttendanceMapper;
import com.nec.middleware.hr.repository.TrainingAttendanceRecordRepository;
import com.nec.middleware.hr.repository.TrainingClassRepository;
import com.nec.middleware.hr.repository.TrainingScheduleRepository;
import com.nec.middleware.hr.repository.TrainingTraineeAllocationRepository;
import com.nec.middleware.hr.repository.UniversityTraineeRepository;
import com.nec.middleware.hr.service.TrainingAttendanceService;
import com.nec.middleware.hr.specification.TrainingAttendanceSearchSpecification;
import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingAttendanceServiceImpl
        implements TrainingAttendanceService {

    private final TrainingAttendanceRecordRepository attendanceRepository;
    private final TrainingClassRepository trainingClassRepository;
    private final UniversityTraineeRepository universityTraineeRepository;
    private final TrainingTraineeAllocationRepository traineeAllocationRepository;
    private final TrainingScheduleRepository trainingScheduleRepository;
    private final TrainingAttendanceMapper attendanceMapper;
    private final UniqueIdGeneratorService uniqueIdGeneratorService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public TrainingAttendanceResponseDto createAttendance(
            Long trainingClassId,
            LocalDate attendanceDate,
            String attendanceRecords,
            MultipartFile signedSheet) {

        TrainingClass trainingClass =
                trainingClassRepository.findById(trainingClassId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        TrainingAttendanceConstants.TRAINING_CLASS_NOT_FOUND
                                ));

        validateAttendanceDate(trainingClass.getId(), attendanceDate);

        List<AttendanceRecordRequestDto> attendanceRecordList =
                parseAttendanceRecords(attendanceRecords);

        String signedSheetUrl = buildSignedSheetUrl(signedSheet);

        List<TrainingAttendanceRecord> savedRecords =
                attendanceRecordList.stream()
                        .map(record -> createAttendanceRecord(
                                attendanceDate,
                                signedSheetUrl,
                                record,
                                trainingClass
                        ))
                        .toList();

        return attendanceMapper.toTrainingAttendanceResponse(savedRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingAttendanceResponseDto> getAllAttendance(
            TrainingAttendanceFilterRequestDto filterDto,
            int page,
            int size) {

        if (filterDto == null) {
            filterDto = new TrainingAttendanceFilterRequestDto();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<TrainingAttendanceRecord> attendancePage =
                attendanceRepository.findAll(
                        TrainingAttendanceSearchSpecification.build(filterDto),
                        pageable
                );

        List<TrainingAttendanceResponseDto> responseList =
                attendancePage.getContent()
                        .stream()
                        .map(record -> attendanceMapper
                                .toTrainingAttendanceResponse(List.of(record)))
                        .toList();

        return new PageImpl<>(
                responseList,
                pageable,
                attendancePage.getTotalElements()
        );
    }

    @Override
    @Transactional
    public TrainingAttendanceResponseDto changeStatus(
            Long id,
            Boolean isActive) {

        TrainingAttendanceRecord attendance =
                attendanceRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        TrainingAttendanceConstants.ATTENDANCE_NOT_FOUND
                                ));

        attendance.setIsActive(isActive);

        TrainingAttendanceRecord updated =
                attendanceRepository.save(attendance);

        return attendanceMapper.toTrainingAttendanceResponse(List.of(updated));
    }

    private TrainingAttendanceRecord createAttendanceRecord(
            LocalDate attendanceDate,
            String signedSheetUrl,
            AttendanceRecordRequestDto recordDto,
            TrainingClass trainingClass) {

        UniversityTrainee trainee =
                universityTraineeRepository.findById(
                                recordDto.getUniversityTraineeId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        TrainingAttendanceConstants.TRAINEE_NOT_FOUND
                                ));

        validateTraineeAllocation(trainingClass.getId(), trainee.getId());

        attendanceRepository
                .findByTrainingClassIdAndTraineeIdAndAttendanceDate(
                        trainingClass.getId(),
                        trainee.getId(),
                        attendanceDate
                )
                .ifPresent(existing -> {
                    throw new ResourceAlreadyExistsException(
                            TrainingAttendanceConstants.ATTENDANCE_ALREADY_EXISTS
                    );
                });

        TrainingAttendanceRecord attendance =
                TrainingAttendanceRecord.builder()
                        .attendanceCode(generateAttendanceNumber())
                        .trainingClass(trainingClass)
                        .trainee(trainee)
                        .attendanceDate(attendanceDate)
                        .isPresent(recordDto.getIsPresent())
                        .signedSheetUrl(signedSheetUrl)
                        .build();

        return attendanceRepository.save(attendance);
    }

    private void validateTraineeAllocation(
            Long trainingClassId,
            Long traineeId) {

        boolean isAllocated =
                traineeAllocationRepository
                        .existsByTrainingClassIdAndTraineeIdAndIsActiveTrue(
                                trainingClassId,
                                traineeId
                        );

        if (!isAllocated) {
            throw new ResourceNotFoundException(
                    TrainingAttendanceConstants.TRAINEE_NOT_ALLOCATED_TO_CLASS
            );
        }
    }

    private void validateAttendanceDate(
            Long trainingClassId,
            LocalDate attendanceDate) {

        TrainingSchedule schedule =
                trainingScheduleRepository.findByTrainingClassId(trainingClassId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        TrainingAttendanceConstants.TRAINING_SCHEDULE_NOT_FOUND
                                ));

        if (attendanceDate.isBefore(schedule.getFromDate())
                || attendanceDate.isAfter(schedule.getToDate())) {

            throw new IllegalArgumentException(
                    TrainingAttendanceConstants.ATTENDANCE_DATE_OUT_OF_SCHEDULE
            );
        }
    }

    private List<AttendanceRecordRequestDto> parseAttendanceRecords(
            String attendanceRecords) {

        try {
            return objectMapper.readValue(
                    attendanceRecords,
                    new TypeReference<List<AttendanceRecordRequestDto>>() {
                    }
            );
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Invalid attendance records format"
            );
        }
    }

    private String buildSignedSheetUrl(MultipartFile signedSheet) {

        if (signedSheet == null || signedSheet.isEmpty()) {
            return null;
        }

        return signedSheet.getOriginalFilename();
    }

    private String generateAttendanceNumber() {

        String prefix = TrainingAttendanceConstants.CODE_PREFIX + "-"
                + Year.now().getValue() + "-";

        return uniqueIdGeneratorService.generateId(
                ModuleCode.TRAINING_ATTENDANCE,
                prefix
        );
    }
}
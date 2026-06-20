package com.nec.middleware.hr.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nec.middleware.dto.IdValueDto;
import com.nec.middleware.exception.ResourceAlreadyExistsException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.constant.TrainingAttendanceConstants;
import com.nec.middleware.hr.dto.request.*;
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

import com.nec.middleware.hr.dto.response.AttendanceSaveSummaryDto;
import com.nec.middleware.hr.dto.response.SaveTrainingAttendanceResponseDto;
import com.nec.middleware.hr.dto.response.TrainingAttendanceGridResponseDto;
import com.nec.middleware.hr.dto.response.TraineeAttendanceGridDto;
import com.nec.middleware.hr.dto.response.SignedAttendanceResponseDto;
import com.nec.middleware.hr.entity.TrainingAttendanceSignedSheet;
import com.nec.middleware.hr.repository.TrainingAttendanceSignedSheetRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final TrainingAttendanceSignedSheetRepository signedSheetRepository;
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

    @Override
    @Transactional
    public SaveTrainingAttendanceResponseDto saveAttendance(
            TrainingAttendanceRequestDtos request) {

        TrainingClass trainingClass =
                trainingClassRepository.findById(
                        request.getClassId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                TrainingAttendanceConstants.TRAINING_CLASS_NOT_FOUND
                        ));

        int savedRecords = 0;

        for (AttendanceEntrysDto traineeAttendance
                : request.getAttendanceEntries()) {

            UniversityTrainee trainee =
                    universityTraineeRepository.findById(
                            traineeAttendance.getTraineeId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    TrainingAttendanceConstants.TRAINEE_NOT_FOUND
                            ));

            validateTraineeAllocation(
                    trainingClass.getId(),
                    trainee.getId()
            );

            for (AttendanceRecordDto attendanceRecord
                    : traineeAttendance.getAttendanceRecords()) {

                validateAttendanceDate(
                        trainingClass.getId(),
                        attendanceRecord.getAttendanceDate()
                );

                attendanceRepository
                        .findByTrainingClassIdAndTraineeIdAndAttendanceDate(
                                trainingClass.getId(),
                                trainee.getId(),
                                attendanceRecord.getAttendanceDate()
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
                                .attendanceDate(
                                        attendanceRecord.getAttendanceDate()
                                )
                                .isPresent(
                                        "PRESENT".equalsIgnoreCase(
                                                attendanceRecord.getStatus()
                                        )
                                )
                                .build();

                attendanceRepository.save(attendance);

                savedRecords++;
            }
        }

        int totalTrainees =
                request.getAttendanceEntries().size();

        AttendanceSaveSummaryDto summary =
                AttendanceSaveSummaryDto.builder()
                        .totalTrainees(totalTrainees)
                        .totalAttendanceRecords(savedRecords)
                        .savedRecords(savedRecords)
                        .build();

        return SaveTrainingAttendanceResponseDto.builder()
                .success(true)
                .message("Attendance saved successfully")
                .summary(summary)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingAttendanceGridResponseDto getAttendance(
            GetTrainingAttendanceRequestDto request) {

        TrainingClass trainingClass =
                trainingClassRepository.findById(request.getClassId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        TrainingAttendanceConstants.TRAINING_CLASS_NOT_FOUND
                                ));

        TrainingSchedule schedule =
                trainingScheduleRepository.findByTrainingClassId(request.getClassId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        TrainingAttendanceConstants.TRAINING_SCHEDULE_NOT_FOUND
                                ));

        List<LocalDate> scheduleDates = new ArrayList<>();

        LocalDate currentDate = schedule.getFromDate();

        while (!currentDate.isAfter(schedule.getToDate())) {
            scheduleDates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        List<Long> traineeIds =
                traineeAllocationRepository
                        .findByTrainingClassIdAndIsActiveTrue(request.getClassId())
                        .stream()
                        .map(allocation -> allocation.getTrainee().getId())
                        .toList();

        List<UniversityTrainee> trainees =
                universityTraineeRepository.findAllById(traineeIds);

        List<TrainingAttendanceRecord> attendanceRecords =
                attendanceRepository.findByTrainingClassIdAndTraineeIdIn(
                        request.getClassId(),
                        traineeIds
                );

        Map<Long, Map<LocalDate, TrainingAttendanceRecord>> attendanceMap =
                attendanceRecords.stream()
                        .collect(Collectors.groupingBy(
                                record -> record.getTrainee().getId(),
                                Collectors.toMap(
                                        TrainingAttendanceRecord::getAttendanceDate,
                                        record -> record
                                )
                        ));

        List<TraineeAttendanceGridDto> traineeGrid =
                trainees.stream()
                        .map(trainee -> buildTraineeAttendanceGrid(
                                trainee,
                                scheduleDates,
                                attendanceMap.get(trainee.getId())
                        ))
                        .toList();

        return TrainingAttendanceGridResponseDto.builder()
                .classId(trainingClass.getId())
                .className(trainingClass.getClassName())
                .universityId(
                        IdValueDto.builder()
                                .id(request.getUniversityId())
                                .build()
                )
                .scheduleDates(scheduleDates)
                .trainees(traineeGrid)
                .build();
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

    @Override
    @Transactional
    public SignedAttendanceResponseDto uploadSignedAttendanceSheet(
            UploadSignedAttendanceRequestDto request,
            MultipartFile signedSheet) {

        TrainingClass trainingClass =
                trainingClassRepository.findById(request.getTrainingClassId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        TrainingAttendanceConstants.TRAINING_CLASS_NOT_FOUND
                                ));

        if (signedSheet == null || signedSheet.isEmpty()) {
            throw new IllegalArgumentException(
                    "Signed attendance sheet is required"
            );
        }

        String filePath = saveSignedSheetToLocalFolder(signedSheet);

        TrainingAttendanceSignedSheet signedAttendance =
                TrainingAttendanceSignedSheet.builder()
                        .trainingClass(trainingClass)
                        .universityId(request.getUniversityId())
                        .fromDate(request.getFromDate())
                        .toDate(request.getToDate())
                        .filePath(filePath)
                        .uploadedBy(request.getUploadedBy())
                        .build();

        TrainingAttendanceSignedSheet saved =
                signedSheetRepository.save(signedAttendance);

        return SignedAttendanceResponseDto.builder()
                .id(saved.getId())
                .trainingClassId(saved.getTrainingClass().getId())
                .universityId(saved.getUniversityId())
                .fromDate(saved.getFromDate())
                .toDate(saved.getToDate())
                .filePath(saved.getFilePath())
                .uploadedBy(saved.getUploadedBy())
                .build();
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
    // helper method

    private TraineeAttendanceGridDto buildTraineeAttendanceGrid(
            UniversityTrainee trainee,
            List<LocalDate> scheduleDates,
            Map<LocalDate, TrainingAttendanceRecord> traineeAttendanceMap) {

        Map<String, String> attendance = new LinkedHashMap<>();

        int daysAttended = 0;

        for (LocalDate date : scheduleDates) {

            TrainingAttendanceRecord record =
                    traineeAttendanceMap == null
                            ? null
                            : traineeAttendanceMap.get(date);

            String status = null;

            if (record != null) {

                status = Boolean.TRUE.equals(record.getIsPresent())
                        ? "PRESENT"
                        : "ABSENT";

                if (Boolean.TRUE.equals(record.getIsPresent())) {
                    daysAttended++;
                }
            }

            // IMPORTANT: Always add date
            attendance.put(date.toString(), status);
        }

        return TraineeAttendanceGridDto.builder()
                .traineeId(trainee.getId())
                .traineeCode(trainee.getUniversityTraineeId())
                .attendance(attendance)
                .daysAttended(daysAttended)
                .totalScheduledDays(scheduleDates.size())
                .build();
    }

    private String saveSignedSheetToLocalFolder(
            MultipartFile signedSheet) {

        try {
            String uploadDir = "C:/nec-uploads/training-attendance";

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + signedSheet.getOriginalFilename();

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(
                    signedSheet.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return filePath.toString();

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Failed to upload signed attendance sheet",
                    exception
            );
        }
    }
}
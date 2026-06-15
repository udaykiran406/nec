package com.nec.middleware.hr.service.impl;

import com.nec.middleware.exception.ResourceAlreadyExistsException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.constant.TrainingScheduleConstants;
import com.nec.middleware.hr.dto.request.TrainingScheduleFilterRequestDto;
import com.nec.middleware.hr.dto.request.TrainingScheduleRequestDto;
import com.nec.middleware.hr.dto.response.TrainingScheduleResponseDto;
import com.nec.middleware.hr.entity.TrainingSchedule;
import com.nec.middleware.hr.mapper.TrainingScheduleMapper;
import com.nec.middleware.hr.repository.TrainingClassRepository;
import com.nec.middleware.hr.repository.TrainingScheduleRepository;
import com.nec.middleware.hr.service.TrainingScheduleService;
import com.nec.middleware.hr.specification.TrainingScheduleSearchSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingScheduleServiceImpl implements TrainingScheduleService {

    private final TrainingScheduleRepository trainingScheduleRepository;
    private final TrainingScheduleMapper trainingScheduleMapper;
    private final TrainingClassRepository trainingClassRepository;

    @Override
    public TrainingScheduleResponseDto createSchedule(
            TrainingScheduleRequestDto request) {

        TrainingSchedule savedEntity =
                trainingScheduleRepository.save(
                        toTrainingScheduleEntity(request)
                );

        return trainingScheduleMapper.toTrainingScheduleResponse(savedEntity);
    }

    @Override
    public TrainingScheduleResponseDto updateSchedule(
            String scheduleCode,
            TrainingScheduleRequestDto request) {

        TrainingSchedule entity =
                trainingScheduleRepository.findByScheduleCode(scheduleCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        TrainingScheduleConstants.SCHEDULE_NOT_FOUND
                                                + scheduleCode
                                ));

        populateTrainingSchedule(entity, request);

        TrainingSchedule updatedEntity =
                trainingScheduleRepository.save(entity);

        return trainingScheduleMapper.toTrainingScheduleResponse(updatedEntity);
    }

    @Override
    public TrainingScheduleResponseDto getScheduleByScheduleCode(
            String scheduleCode) {

        TrainingSchedule entity =
                trainingScheduleRepository.findByScheduleCode(scheduleCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        TrainingScheduleConstants.SCHEDULE_NOT_FOUND
                                                + scheduleCode
                                ));

        return trainingScheduleMapper.toTrainingScheduleResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingScheduleResponseDto> getAllSchedules(
            TrainingScheduleFilterRequestDto request,
            int page,
            int size) {

        if (request == null) {
            request = new TrainingScheduleFilterRequestDto();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return trainingScheduleRepository.findAll(
                        TrainingScheduleSearchSpecification.build(request),
                        pageable
                )
                .map(trainingScheduleMapper::toTrainingScheduleResponse);
    }

    @Override
    public TrainingScheduleResponseDto changeStatus(
            String scheduleCode,
            Boolean isActive) {

        TrainingSchedule entity =
                trainingScheduleRepository.findByScheduleCode(scheduleCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        TrainingScheduleConstants.SCHEDULE_NOT_FOUND
                                                + scheduleCode
                                ));

        entity.setIsActive(isActive);

        TrainingSchedule updatedEntity =
                trainingScheduleRepository.save(entity);

        return trainingScheduleMapper.toTrainingScheduleResponse(updatedEntity);
    }

    private void populateTrainingSchedule(
            TrainingSchedule entity,
            TrainingScheduleRequestDto request) {

        entity.setFromDate(request.getFromDate());
        entity.setToDate(request.getToDate());
        entity.setDuration(calculateDuration(
                request.getFromDate(),
                request.getToDate()
        ));
        entity.setTimeSlot(request.getTimeSlot());
        entity.setVenue(request.getVenue());
        entity.setLocation(request.getLocation());

        if (request.getTrainingClassId() != null) {
            entity.setTrainingClass(
                    trainingClassRepository.findById(request.getTrainingClassId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Training class not found"
                                    ))
            );
        }
    }

    private TrainingSchedule toTrainingScheduleEntity(
            TrainingScheduleRequestDto request) {

        TrainingSchedule entity = new TrainingSchedule();

        entity.setScheduleCode(
                generateScheduleCode(
                        TrainingScheduleConstants.CODE_PREFIX,
                        TrainingScheduleConstants.CODE_PAD
                )
        );

        entity.setTrainingClass(
                trainingClassRepository.findById(request.getTrainingClassId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Training class not found"
                                ))
        );

        entity.setFromDate(request.getFromDate());
        entity.setToDate(request.getToDate());
        entity.setDuration(calculateDuration(
                request.getFromDate(),
                request.getToDate()
        ));
        entity.setTimeSlot(request.getTimeSlot());
        entity.setVenue(request.getVenue());
        entity.setLocation(request.getLocation());

        return entity;
    }

    private Integer calculateDuration(
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate) {

        return Math.toIntExact(
                ChronoUnit.DAYS.between(fromDate, toDate) + 1
        );
    }

    private String generateScheduleCode(
            String codePrefix,
            int codePad) {

        int next = trainingScheduleRepository.findMaxCodeSequence() + 1;

        return codePrefix + String.format("%0" + codePad + "d", next);
    }
}